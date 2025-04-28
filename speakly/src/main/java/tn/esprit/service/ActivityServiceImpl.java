package tn.esprit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import tn.esprit.entity.Activity;
import tn.esprit.entity.WaitlistRegistration;
import tn.esprit.exception.ResourceNotFoundException;
import tn.esprit.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import tn.esprit.repository.WaitlistRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityServiceImpl implements IActivityService {

    @Value("${Groq.api.url}")
    private String apiUrl;

    @Value("${Groq.api.token}")
    private String apiToken;
    private final ActivityRepository activityRepository;
    private final WaitlistRepository waitlistRepository;
    private final WaitlistService waitlistService;

    @Autowired
    private EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);
    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, WaitlistRepository waitlistRepository, WaitlistService waitlistService, EmailService emailService) {
        this.activityRepository = activityRepository;
        this.waitlistRepository = waitlistRepository;
        this.waitlistService = waitlistService;
        this.emailService = emailService;
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findByDateGreaterThanEqual(LocalDate.now());
    }

    @Override
    public Activity updateActivity(Long id, Activity updatedActivity) {
        return activityRepository.findById(id).map(activity -> {
            activity.setName(updatedActivity.getName());
            activity.setDetails(updatedActivity.getDetails());
            activity.setLocation(updatedActivity.getLocation());
            activity.setDate(updatedActivity.getDate());
            activity.setAvailableSeats(updatedActivity.getAvailableSeats());
            activity.setPrice(updatedActivity.getPrice());

            // Mise à jour de l'image uniquement si une nouvelle image est envoyée
            if (updatedActivity.getImagePath() != null && !updatedActivity.getImagePath().isEmpty()) {
                activity.setImagePath(updatedActivity.getImagePath());
            }

            return activityRepository.save(activity);
        }).orElseThrow(() -> new ResourceNotFoundException("Activity not found with ID: " + id));
    }

    @Override
    public Activity updateAvailableSeats(Long id, int seats) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        int oldSeats = activity.getAvailableSeats();
        activity.setAvailableSeats(seats); // Utilisez directement la nouvelle valeur

        Activity updatedActivity = activityRepository.save(activity);

        // Si des places sont ajoutées (état précédent = 0)
        if (oldSeats == 0 && seats > 0) {
            waitlistService.checkAndNotifyForActivity(id);
        }

        return updatedActivity;
    }

    @Override
    public List<Activity> findByDate(LocalDate date) {
        return activityRepository.findByDate(date);
    }

    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return activityRepository.existsById(id);
    }

    @Override
    public List<Activity> recommanderActivitesSelonJour() {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Find activities happening today
        List<Activity> todayActivities = activityRepository.findByDate(today);

        // If no activities today, you could add fallback logic like:
        // - Get upcoming activities
        // - Get popular activities
        // - Get random activities

        return todayActivities;
    }
    @Scheduled(cron = "0 0 1 * * ?") // Exécution quotidienne à 1h du matin
    public void supprimerActivitesPassees() {
        LocalDate aujourdhui = LocalDate.now();
        List<Activity> activitesPassees = activityRepository.findByDateBefore(aujourdhui);

        if (!activitesPassees.isEmpty()) {
            activityRepository.deleteAll(activitesPassees);
            // Loguer le nombre d'activités supprimées
            logger.info("{} activités passées ont été supprimées.", activitesPassees.size());

        }
    }



    @Override
    public void addToWaitlist(Long activityId, String email) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        if (activity.getAvailableSeats() > 0) {
            throw new IllegalStateException("Activity has available seats");
        }

        WaitlistRegistration registration = new WaitlistRegistration();
        registration.setActivity(activity);
        registration.setEmail(email);
        registration.setRegistrationDate(LocalDateTime.now());

        waitlistRepository.save(registration);
    }

    @Scheduled(fixedRate = 60000) // Toutes les 5 minutes
    public void checkWaitlistAvailability() {
        List<Activity> activitiesWithNewSeats = activityRepository.findActivitiesWithAvailableSeats();

        activitiesWithNewSeats.forEach(activity -> {
            List<WaitlistRegistration> waitlist = waitlistRepository
                    .findByActivityAndNotifiedOrderByRegistrationDateAsc(activity, false);

            int seatsToAllocate = Math.min(activity.getAvailableSeats(), waitlist.size());

            if (seatsToAllocate > 0) {
                List<WaitlistRegistration> toNotify = waitlist.subList(0, seatsToAllocate);

                toNotify.forEach(registration -> {
                    notifyUser(registration);
                    registration.setNotified(true);
                });

                waitlistRepository.saveAll(toNotify);
                activity.setAvailableSeats(activity.getAvailableSeats() - seatsToAllocate);
                activityRepository.save(activity);
            }
        });
    }

    private void notifyUser(WaitlistRegistration registration) {
        Activity activity = registration.getActivity();
        String subject = "Place disponible pour " + activity.getName();
        String message = String.format(
                "Bonjour,\n\n" +
                        "Une place s'est libérée pour l'activité '%s'.\n\n" +
                        "Détails :\n" +
                        "- Date : %s\n" +
                        "- Lieu : %s\n" +
                        "- Prix : %.2f TND\n\n" +
                        "<a href='http://localhost:4200/activities/%d' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; border-radius: 5px;'>" +
                        "Réserver maintenant</a>\n\n" +
                        "Ce lien expire dans 24 heures.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe des activités",
                activity.getName(),
                activity.getDate().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)),
                activity.getLocation(),
                activity.getPrice(),
                activity.getActivityId()
        );

        emailService.sendHtmlEmail(registration.getEmail(), subject, message);
    }

    @Override
    public List<Activity> smartSearchNlp(String query) {
        List<Activity> allActivities = activityRepository.findUpcomingActivities();
        String lowerQuery = query.toLowerCase();

        return allActivities.stream()
                .filter(activity -> matchesSearch(activity, lowerQuery))
                .sorted((a1, a2) -> compareRelevance(a1, a2, lowerQuery))
                .collect(Collectors.toList());
    }

    private boolean matchesSearch(Activity activity, String lowerQuery) {
        // Combine all searchable text
        String searchText = (activity.getName() + " " +
                activity.getDetails() + " " +
                activity.getLocation() + " " +
                activity.getPrice()).toLowerCase();

        // Special cases
        if (lowerQuery.contains("weekend")) {
            return isWeekendActivity(activity.getDate());
        }
        if (lowerQuery.contains("cheap") || lowerQuery.contains("affordable")) {
            return activity.getPrice() < 20;
        }
        if (lowerQuery.contains("free") || lowerQuery.contains("For free")) {
            return activity.getPrice() == 0 ;
        }
        if (lowerQuery.contains("family") || lowerQuery.contains("kids")) {
            return searchText.contains("child") || searchText.contains("kid") ||
                    searchText.contains("family");
        }

        // Default keyword search
        return searchText.contains(lowerQuery);
    }

    private boolean isWeekendActivity(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private int compareRelevance(Activity a1, Activity a2, String query) {
        // Prioritize matches in name over details
        int score1 = calculateMatchScore(a1, query);
        int score2 = calculateMatchScore(a2, query);
        return Integer.compare(score2, score1); // Descending order
    }

    private int calculateMatchScore(Activity activity, String query) {
        int score = 0;
        if (activity.getName().toLowerCase().contains(query)) score += 3;
        if (activity.getDetails().toLowerCase().contains(query)) score += 2;
        if (activity.getLocation().toLowerCase().contains(query)) score += 1;
        return score;
    }
    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean containsAny(String text, String... terms) {
        return Arrays.stream(terms).anyMatch(text::contains);
    }

    private int calculateRelevanceScore(Activity activity, String query) {
        String text = activity.getName() + " " + activity.getDetails();
        String[] keywords = query.toLowerCase().split(" ");
        return (int) Arrays.stream(keywords)
                .filter(keyword -> text.toLowerCase().contains(keyword))
                .count();
    }

    public List<Activity> simpleSearch(String query) {
        return activityRepository.fullTextSearch(query);
    }
    private List<List<Double>> getEmbeddings(List<String> texts) {
        try {
            if (texts == null || texts.isEmpty()) {
                return Collections.emptyList();
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("input", texts);
            payload.put("model", "text-embedding-3-small"); // Updated model name

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Groq API request failed with status: {}", response.getStatusCode());
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            List<List<Double>> embeddings = new ArrayList<>();
            for (JsonNode item : root.get("data")) {
                List<Double> embedding = new ArrayList<>();
                for (JsonNode val : item.get("embedding")) {
                    embedding.add(val.asDouble());
                }
                embeddings.add(embedding);
            }
            return embeddings;
        } catch (Exception e) {
            logger.error("Error getting embeddings from Groq API", e);
            return null;
        }
    }
    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += v1.get(i) * v1.get(i);
            norm2 += v2.get(i) * v2.get(i);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    @Override
    public void notifyWaitlist(Long activityId) {
        waitlistService.checkAndNotifyForActivity(activityId);
    }
}