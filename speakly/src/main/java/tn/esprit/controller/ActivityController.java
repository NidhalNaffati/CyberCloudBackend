package tn.esprit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.entity.Activity;
import tn.esprit.entity.WaitlistRegistration;
import tn.esprit.exception.ResourceNotFoundException;
import tn.esprit.repository.WaitlistRepository;
import tn.esprit.service.ActivityImageService;
import tn.esprit.service.IActivityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.service.WaitlistService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Value("${huggingface.api.url}")
    private String hfApiUrl;

    @Value("${huggingface.api.token}")
    private String hfApiToken;


    @Value("${Groq.api.url}")
    private String groqApiUrl;

    @Value("${Groq.api.token}")
    private String groqApiToken;
    private final IActivityService activityService;
    private final WaitlistRepository waitlistRepository;
    private final ActivityImageService activityImageService;
    private final WaitlistService waitlistService;
    private final String uploadDir;


    public ActivityController(IActivityService activityService, WaitlistRepository waitlistRepository, ActivityImageService activityImageService, WaitlistService waitlistService) {
        this.activityService = activityService;
        this.waitlistRepository = waitlistRepository;
        this.activityImageService = activityImageService;
        this.waitlistService = waitlistService;
        this.uploadDir = System.getProperty("user.home") + "/uploads/";
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    @GetMapping("/search-nlp")
    public List<Activity> smartSearch(@RequestParam String q) {
        return activityService.smartSearchNlp(q);
    }
    @PostMapping("/ai/describe")
    public ResponseEntity<String> generateDescription(@RequestBody AiPrompt prompt) {
        if (prompt.getTitle() == null || prompt.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title cannot be empty");
        }

        // Try Hugging Face first
        String description = callHuggingFaceWithRetry(prompt.getTitle(), 3); // Retry 3 times

        // Fallback to Groq if Hugging Face fails
        if (description.startsWith("Failed") || description.contains("unavailable")) {
            description = callGroqApi(prompt.getTitle());
        }

        return ResponseEntity.ok(description);
    }
    private String callGroqApi(String title) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Configure timeout
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
                    .setConnectTimeout(10000); // 10 seconds timeout

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + groqApiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Build the proper request format for Groq's current API
            Map<String, Object> request = new HashMap<>();
            request.put("model", "llama3-70b-8192"); // Current recommended model
            request.put("messages", List.of(
                    Map.of("role", "user",
                            "content", "Write a 200-word engaging description for an activity titled: '" +
                                    title + "'. Include objectives, target audience, and benefits.")
            ));
            request.put("max_tokens", 350); // Enough for ~200 words
            request.put("temperature", 0.7); // Creativity control

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // Make the API call
            ResponseEntity<String> response = restTemplate.postForEntity(
                    groqApiUrl,
                    entity,
                    String.class
            );

            // Process response
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getBody());

                // Extract the generated text
                return jsonNode.path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
            } else {
                // Handle API errors
                ObjectMapper mapper = new ObjectMapper();
                JsonNode errorNode = mapper.readTree(response.getBody());
                return "Groq API Error: " + errorNode.path("error").path("message").asText();
            }
        } catch (Exception e) {
            return "Failed to call Groq API: " + e.getMessage();
        }
    }
    private String callHuggingFaceWithRetry(String title, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            String result = callHuggingFace(title);
            if (!result.contains("unavailable") && !result.contains("loading")) {
                return result;
            }
            try {
                Thread.sleep(5000); // Wait 5s between retries
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Request interrupted.";
            }
        }
        return "Hugging Face API is temporarily unavailable.";
    }

    private String callHuggingFace(String title) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + hfApiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Simplify prompt for faster response
            String prompt = "Write a 1-paragraph description for: " + title;
            Map<String, Object> payload = Map.of(
                    "inputs", prompt,
                    "parameters", Map.of("max_length", 150)
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    hfApiUrl,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getBody());
                return jsonNode.get(0).get("generated_text").asText();
            } else {
                return "API Error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            return "Failed to call Hugging Face: " + e.getMessage();
        }
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> createActivity(
            @RequestParam("name") String name,
            @RequestParam("details") String details,
            @RequestParam("location") String location,
            @RequestParam("availableSeats") int availableSeats,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("price") double price,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        try {
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                imageUrl = activityImageService.uploadActivityImage(file); // Upload image to Cloudinary
            }

            Activity activity = new Activity();
            activity.setName(name);
            activity.setDetails(details);
            activity.setLocation(location);
            activity.setAvailableSeats(availableSeats);
            activity.setDate(date);
            activity.setPrice(price);
            activity.setImagePath(imageUrl); // Store the Cloudinary image URL

            activityService.createActivity(activity);

            return ResponseEntity.ok(new ResponseMessage("Activité ajoutée avec succès !"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Erreur lors de l'ajout de l'activité"));
        }
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        file.transferTo(filePath);
        return fileName;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with ID: " + id));
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Resource resource = new FileSystemResource(filePath);

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            }
        } catch (IOException e) {
            // Log the error
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> updateActivity(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("details") String details,
            @RequestParam("location") String location,
            @RequestParam("availableSeats") int availableSeats,
            @RequestParam("date") String date,
            @RequestParam("price") double price,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            Activity existingActivity = activityService.getActivityById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Activity not found with ID: " + id));

            existingActivity.setName(name);
            existingActivity.setDetails(details);
            existingActivity.setLocation(location);
            existingActivity.setAvailableSeats(availableSeats);
            existingActivity.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            existingActivity.setPrice(price);

            if (image != null && !image.isEmpty()) {
                String imageUrl = activityImageService.uploadActivityImage(image); // Upload image to Cloudinary
                existingActivity.setImagePath(imageUrl); // Update with Cloudinary URL
            }

            activityService.updateActivity(id, existingActivity);
            return ResponseEntity.ok(new ResponseMessage("Activité mise à jour avec succès !"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Erreur lors de la mise à jour"));
        }
    }

    @PatchMapping("/{id}/seats")
    public ResponseEntity<Activity> updateSeats(
            @PathVariable Long id,
            @RequestParam int seats) {

        Activity updatedActivity = activityService.updateAvailableSeats(id, seats);

        // Vérification automatique de la waitlist
        if (seats > 0) {
            activityService.notifyWaitlist(id);
        }

        return ResponseEntity.ok(updatedActivity);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable("id") Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/today")
    public ResponseEntity<List<Activity>> getTodayActivities() {
        List<Activity> todayActivities = activityService.recommanderActivitesSelonJour();
        return ResponseEntity.ok(todayActivities);
    }
    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupPastActivities() {
        activityService.supprimerActivitesPassees();
        return ResponseEntity.noContent().build();
    }
    // ActivityController.java
    @GetMapping("/{activityId}/waitlist/check")
    public ResponseEntity<?> checkWaitlistStatus(@PathVariable Long activityId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = authentication.getName();
        boolean isOnWaitlist = waitlistService.isUserInWaitlist(activityId, userEmail);

        return ResponseEntity.ok(isOnWaitlist);
    }

    @PostMapping("/{activityId}/waitlist")
    public ResponseEntity<?> joinWaitlist(@PathVariable Long activityId) {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessage("Utilisateur non authentifié"));
        }

        String userEmail = authentication.getName();
        Activity activity = activityService.getActivityById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        if (activity.getAvailableSeats() > 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Des places sont encore disponibles"));
        }

        if (waitlistRepository.existsByActivityAndEmail(activity, userEmail)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Vous êtes déjà sur la liste d'attente"));
        }

        WaitlistRegistration registration = new WaitlistRegistration();
        registration.setActivity(activity);
        registration.setEmail(userEmail);
        registration.setRegistrationDate(LocalDateTime.now());

        waitlistRepository.save(registration);

        return ResponseEntity.ok()
                .body(new ResponseMessage("Inscription confirmée ! Vous serez notifié si une place se libère"));
    }

}