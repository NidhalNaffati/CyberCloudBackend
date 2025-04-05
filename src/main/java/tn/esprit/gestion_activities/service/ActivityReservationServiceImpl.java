package tn.esprit.gestion_activities.service;

import tn.esprit.gestion_activities.entity.ActivityReservation;
import tn.esprit.gestion_activities.repository.ActivityReservationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityReservationServiceImpl implements IActivityReservationService {

    private final ActivityReservationRepository reservationRepository;

    public ActivityReservationServiceImpl(ActivityReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ActivityReservation createReservation(ActivityReservation reservation) {
        // Vérifier si le nombre de places est suffisant avant d'ajouter la réservation
        if (reservation.getNumberOfSeats() > reservation.getActivity().getAvailableSeats()) {
            throw new IllegalArgumentException("Il n'y a pas assez de places disponibles !");
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<ActivityReservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<ActivityReservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public ActivityReservation updateReservation(ActivityReservation reservation) {
        return reservationRepository.save(reservation);
    }
}
