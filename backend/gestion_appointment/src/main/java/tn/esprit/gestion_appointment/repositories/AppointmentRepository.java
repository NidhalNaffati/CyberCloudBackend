package tn.esprit.gestion_appointment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.gestion_appointment.entities.Appointment;


public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
