package tn.esprit.gestion_activities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.gestion_activities.entity.Activity;
import tn.esprit.gestion_activities.entity.WaitlistRegistration;

import java.util.List;

public interface WaitlistRepository extends JpaRepository<WaitlistRegistration, Long> {
    List<WaitlistRegistration> findByActivityAndNotifiedOrderByRegistrationDateAsc(
            Activity activity, boolean notified);

    boolean existsByActivityAndEmail(Activity activity, String email);
}