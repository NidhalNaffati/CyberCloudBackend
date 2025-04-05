package tn.esprit.gestion_activities.controller;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.gestion_activities.entity.Activity;
import tn.esprit.gestion_activities.exception.ResourceNotFoundException;
import tn.esprit.gestion_activities.service.IActivityService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final IActivityService activityService;
    private static final String UPLOAD_DIR = "uploads/"; // Chemin de stockage des images
    @Value("${file.upload-dir}")
    private String uploadDir;

    public ActivityController(IActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> createActivity(
            @RequestParam("name") String name,
            @RequestParam("details") String details,
            @RequestParam("location") String location,
            @RequestParam("availableSeats") int availableSeats,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("price") double price,
            @RequestParam(value = "image", required = false) MultipartFile file) {

        try {
            // Dossier de téléchargement
            String uploadDir = System.getProperty("user.home") + "/uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // Création du répertoire s'il n'existe pas
            }

            String filePath = null;
            if (file != null && !file.isEmpty()) {
                // Générer un nom de fichier unique
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                filePath = uploadDir + fileName;
                // Sauvegarder le fichier
                file.transferTo(new File(filePath));
            }

            // Créer l'activité
            Activity activity = new Activity();
            activity.setName(name);
            activity.setDetails(details);
            activity.setLocation(location);
            activity.setAvailableSeats(availableSeats);
            activity.setDate(date);
            activity.setPrice(price);
            activity.setImagePath(filePath); // Enregistrer le chemin de l'image

            // Sauvegarder l'activité dans la base de données
            activityService.createActivity(activity);

            // Retourner une réponse JSON valide
            ResponseMessage responseMessage = new ResponseMessage("Activité ajoutée avec succès !");
            return ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            e.printStackTrace(); // Affiche l'erreur dans les logs du serveur
            // Retourner une erreur en cas de problème
            ResponseMessage errorMessage = new ResponseMessage("Erreur lors de l'ajout de l'activité");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        return activity.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with ID: " + id));
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }
    @GetMapping("/activities/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
        // Define the path to the image
        Path path = Paths.get("C:/Users/R I B/Desktop/gestion_activities/src/main/java/tn/esprit/gestion_activities/uploads/activities/images/")
                .resolve(imageName);

        // Create a Resource object from the file path
        Resource resource = (Resource) new FileSystemResource(path);

        // Check if the file exists
        if (!((FileSystemResource) resource).exists()) {
            return ResponseEntity.notFound().build();
        }

        // Get the content type of the file
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Return the image as a response
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage> updateActivity(@PathVariable("id") Long id,
                                                          @RequestParam("name") String name,
                                                          @RequestParam("details") String details,
                                                          @RequestParam("location") String location,
                                                          @RequestParam("availableSeats") int availableSeats,
                                                          @RequestParam("date") String date,
                                                          @RequestParam("price") double price,
                                                          @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        Optional<Activity> existingActivityOptional = activityService.getActivityById(id);

        if (existingActivityOptional.isPresent()) {
            Activity existingActivity = existingActivityOptional.get();
            existingActivity.setName(name);
            existingActivity.setDetails(details);
            existingActivity.setLocation(location);
            existingActivity.setAvailableSeats(availableSeats);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            existingActivity.setDate(parsedDate);
            existingActivity.setPrice(price);

            if (image != null && !image.isEmpty()) {
                String uploadDir = System.getProperty("user.home") + "/uploads/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                String filePath = uploadDir + fileName;
                image.transferTo(new File(filePath));
                existingActivity.setImagePath(filePath);
            }

            // Appeler la méthode updateActivity avec ID et l'activité mise à jour
            activityService.updateActivity(id, existingActivity);

            ResponseMessage responseMessage = new ResponseMessage("Activité mise à jour avec succès !");
            return ResponseEntity.ok(responseMessage);
        } else {
            throw new ResourceNotFoundException("Activity not found with ID: " + id);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable("id") Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
