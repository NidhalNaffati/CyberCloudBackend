package tn.esprit.gestion_activities.controller;

import jakarta.validation.Valid;
import tn.esprit.gestion_activities.entity.Activity;
import tn.esprit.gestion_activities.entity.ActivityReservation;
import tn.esprit.gestion_activities.exception.ResourceNotFoundException;
import tn.esprit.gestion_activities.service.IActivityReservationService;
import org.springframework.web.bind.annotation.*;
import tn.esprit.gestion_activities.service.IActivityService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/reservations")
public class ActivityReservationController {

    private final IActivityReservationService reservationService;
    private final IActivityService activityService;

    public ActivityReservationController(IActivityReservationService reservationService,
                                         IActivityService activityService) {
        this.reservationService = reservationService;
        this.activityService = activityService;
    }

    @PostMapping("/activity/{activityId}/create")
    public ActivityReservation createReservation(@PathVariable Long activityId,
                                                 @Valid @RequestBody ActivityReservation reservation) {
        Optional<Activity> activityOptional = activityService.getActivityById(activityId);
        if (!activityOptional.isPresent()) {
            throw new ResourceNotFoundException("Activity not found with ID: " + activityId);
        }

        Activity activity = activityOptional.get();
        reservation.setActivity(activity);
        reservation.setReservationDate(new Date());

        return reservationService.createReservation(reservation);
    }

    @GetMapping("/{id}")
    public Optional<ActivityReservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @GetMapping
    public List<ActivityReservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PutMapping("/{id}")
    public ActivityReservation updateReservation(@PathVariable Long id,
                                                 @Valid @RequestBody ActivityReservation updatedReservation) {
        Optional<ActivityReservation> existingReservation = reservationService.getReservationById(id);
        if (!existingReservation.isPresent()) {
            throw new ResourceNotFoundException("Reservation not found with ID: " + id);
        }

        ActivityReservation reservation = existingReservation.get();
        reservation.setFullName(updatedReservation.getFullName());
        reservation.setEmail(updatedReservation.getEmail());
        reservation.setPhoneNumber(updatedReservation.getPhoneNumber());
        reservation.setNumberOfSeats(updatedReservation.getNumberOfSeats());

        return reservationService.updateReservation(reservation);
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }
}