package tn.esprit.gestion_activities.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.gestion_activities.entity.ActivityReservation;
import tn.esprit.gestion_activities.service.EmailService;
import tn.esprit.gestion_activities.repository.ActivityReservationRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ReminderScheduler {

    private final ActivityReservationRepository reservationRepository;
    private final EmailService emailService;

    public ReminderScheduler(ActivityReservationRepository reservationRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    // S'exécute tous les jours à 8h
    @Scheduled(cron = "0 0 8 * * ?")//S m h & M J/S
    public void sendReminders() {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();

        List<ActivityReservation> reservations = reservationRepository.findAll();

        for (ActivityReservation res : reservations) {
            LocalDate activityDate = res.getActivity().getDate();
            if (isSameDay(activityDate, today) || isSameDay(activityDate, tomorrow)) {
                String subject = "📅 Rappel de votre activité";
                String body = String.format(
                        "Bonjour %s,\n\nCeci est un rappel pour votre activité prévue le %s.\n\nÀ bientôt !",
                        res.getFullName(),
                        new SimpleDateFormat("dd/MM/yyyy").format(activityDate)
                );
                emailService.sendSimpleEmail(res.getEmail(), subject, body);
            }
        }
    }

    private boolean isSameDay(LocalDate localDate, Date dateToCompare) {
        if (localDate == null) return false;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        // Conversion LocalDate → Date
        Calendar cal = Calendar.getInstance();
        cal.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth(), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date convertedLocalDate = cal.getTime();

        return fmt.format(convertedLocalDate).equals(fmt.format(dateToCompare));
    }

}
