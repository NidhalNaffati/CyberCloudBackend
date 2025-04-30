package tn.esprit.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.entity.Appointment;
import tn.esprit.entity.User;
import tn.esprit.repository.AppointmentRepository;
import tn.esprit.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment) {
        if (!isAppointmentValid(appointment)) {
            throw new IllegalArgumentException("Rendez-vous invalide (horaire, chevauchement ou jour non autorisé).");
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Récupération de l'utilisateur à partir de l'ID
        if (appointment.getUserId() != null) {

            Optional<User> userOpt = userRepository.findById(appointment.getUserId());
            userOpt.ifPresent(user -> sendAppointmentConfirmationEmail(user, savedAppointment));
        }

        return savedAppointment;
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

    public boolean isAppointmentValid(Appointment newAppointment) {
        // 1. Vérifier si c'est samedi ou dimanche
        DayOfWeek day = newAppointment.getDate().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }

        // 2. Vérifier que le créneau est entre 9h et 17h
        LocalTime start = newAppointment.getStartTime();
        LocalTime end = newAppointment.getEndTime();
        if (start.isBefore(LocalTime.of(9, 0)) || end.isAfter(LocalTime.of(17, 0))) {
            return false;
        }

        // 3. Vérifier la durée maximale
        Duration duration = Duration.between(start, end);
        if (duration.toHours() > 3) {
            return false;
        }

        // 4. Vérifier s’il y a un conflit avec un autre rendez-vous
        List<Appointment> sameDayAppointments = appointmentRepository.findByDate(newAppointment.getDate());
        for (Appointment existing : sameDayAppointments) {
            if (
                (start.isBefore(existing.getEndTime()) && end.isAfter(existing.getStartTime()))
            ) {
                return false; // chevauchement
            }
        }

        return true;
    }

    private void sendAppointmentConfirmationEmail(User user, Appointment appointment) {
        // Formatage de la date et des heures pour l'affichage dans l'email
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String formattedDate = appointment.getDate().format(dateFormatter);
        String startTime = appointment.getStartTime().format(timeFormatter);
        String endTime = appointment.getEndTime().format(timeFormatter);

        // Utilisation du service email existant
        emailService.sendAppointmentConfirmation(
            user.getEmail(),
            user.getFirstName(),
            appointment.getDate().toString(),
            appointment.getStartTime().toString(),
            appointment.getEndTime().toString()
        );

    }
}
