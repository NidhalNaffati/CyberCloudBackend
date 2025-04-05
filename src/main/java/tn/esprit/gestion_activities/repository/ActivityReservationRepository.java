package tn.esprit.gestion_activities.repository;

import tn.esprit.gestion_activities.entity.ActivityReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityReservationRepository extends JpaRepository<ActivityReservation, Long> {
}
