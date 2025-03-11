package tn.esprit.gestion_appointment.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.gestion_appointment.entities.Appointment;
import tn.esprit.gestion_appointment.repositories.AppointmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment newAppointment) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setDate(newAppointment.getDate());
            appointment.setStartTime(newAppointment.getStartTime());
            appointment.setEndTime(newAppointment.getEndTime());
            appointment.setUserId(newAppointment.getUserId());
            appointment.setAvailable(newAppointment.isAvailable());
            return appointmentRepository.save(appointment);
        }).orElse(null);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}

