package tn.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.Complaint;

import java.util.List;

@Repository
public interface IStatisticsRepository extends JpaRepository<Complaint, Integer> {

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.isRead = :isRead")
    long countByReadStatus(@Param("isRead") boolean isRead);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.isUrgent = :isUrgent")
    long countByUrgencyStatus(@Param("isUrgent") boolean isUrgent);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.starRatingConsultation = :rating")
    long countByRating(@Param("rating") int rating);

    @Query("SELECT COUNT(c) FROM Complaint c")
    long countAllComplaints();


    // activity statistics
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.availableSeats = 0")
    long countFullActivities();

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.availableSeats > 0")
    long countAvailableActivities();

    @Query("SELECT COUNT(w) FROM WaitlistRegistration w WHERE w.activity.activityId = :activityId")
    long countWaitlistByActivity(@Param("activityId") Long activityId);

    @Query("SELECT COUNT(r) FROM ActivityReservation r WHERE r.activity.activityId = :activityId")
    long countReservationsByActivity(@Param("activityId") Long activityId);

    @Query("SELECT a.location, COUNT(a) FROM Activity a GROUP BY a.location")
    List<Object[]> countActivitiesByLocation();

    @Query("SELECT FUNCTION('MONTH', a.date), COUNT(a) FROM Activity a GROUP BY FUNCTION('MONTH', a.date)")
    List<Object[]> countActivitiesByMonth();

    @Query("SELECT AVG(a.price) FROM Activity a")
    double getAverageActivityPrice();
}