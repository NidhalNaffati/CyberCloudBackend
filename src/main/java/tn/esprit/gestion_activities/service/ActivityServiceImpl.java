package tn.esprit.gestion_activities.service;

import tn.esprit.gestion_activities.entity.Activity;
import tn.esprit.gestion_activities.exception.ResourceNotFoundException;
import tn.esprit.gestion_activities.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements IActivityService {

    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
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
        return activityRepository.findAll();
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
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return activityRepository.existsById(id);
    }
}
