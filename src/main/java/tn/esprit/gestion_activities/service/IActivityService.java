package tn.esprit.gestion_activities.service;

import tn.esprit.gestion_activities.entity.Activity;

import java.util.List;
import java.util.Optional;

public interface IActivityService {
    Activity createActivity(Activity activity);
    Optional<Activity> getActivityById(Long id);
    List<Activity> getAllActivities();
    Activity updateActivity(Long id, Activity updatedActivity);
    void deleteActivity(Long id);
    boolean existsById(Long id);
}
