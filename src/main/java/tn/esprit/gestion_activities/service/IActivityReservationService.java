package tn.esprit.gestion_activities.service;

import tn.esprit.gestion_activities.entity.ActivityReservation;
import java.util.List;
import java.util.Optional;

public interface IActivityReservationService {
    ActivityReservation createReservation(ActivityReservation reservation);
    Optional<ActivityReservation> getReservationById(Long id);
    List<ActivityReservation> getAllReservations();
    void deleteReservation(Long id);

    ActivityReservation updateReservation(ActivityReservation reservation);
}
