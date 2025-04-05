package tn.esprit.gestion_activities.repository;

import tn.esprit.gestion_activities.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
