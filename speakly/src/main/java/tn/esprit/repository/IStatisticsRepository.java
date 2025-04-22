package tn.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.Complaint;

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
}