package tn.esprit.gestion_appointment.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.gestion_appointment.entities.Consultation;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}
