package tn.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.BlogComment;

import java.util.Date;
import java.util.List;

@Repository
public interface StatisticsCommentsRepository extends JpaRepository<BlogComment, Long> {
    
    // Requête pour compter les commentaires par mois
    @Query("SELECT MONTH(bc.createdAt) as month, YEAR(bc.createdAt) as year, COUNT(bc) as count " +
           "FROM BlogComment bc " +
           "WHERE (:startDate IS NULL OR bc.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR bc.createdAt <= :endDate) " +
           "GROUP BY YEAR(bc.createdAt), MONTH(bc.createdAt) " +
           "ORDER BY year, month")
    List<Object[]> countCommentsByMonth(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Requête pour compter les commentaires par jour
    @Query("SELECT DAY(bc.createdAt) as day, MONTH(bc.createdAt) as month, YEAR(bc.createdAt) as year, COUNT(bc) as count " +
           "FROM BlogComment bc " +
           "WHERE (:startDate IS NULL OR bc.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR bc.createdAt <= :endDate) " +
           "GROUP BY YEAR(bc.createdAt), MONTH(bc.createdAt), DAY(bc.createdAt) " +
           "ORDER BY year, month, day")
    List<Object[]> countCommentsByDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Requête pour compter les commentaires par année
    @Query("SELECT YEAR(bc.createdAt) as year, COUNT(bc) as count " +
           "FROM BlogComment bc " +
           "WHERE (:startDate IS NULL OR bc.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR bc.createdAt <= :endDate) " +
           "GROUP BY YEAR(bc.createdAt) " +
           "ORDER BY year")
    List<Object[]> countCommentsByYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Requête pour obtenir l'activité des utilisateurs (commentaires)
    @Query("SELECT bc.user.id as userId, COUNT(bc) as commentCount " +
           "FROM BlogComment bc " +
           "WHERE bc.user IS NOT NULL " +
           "GROUP BY bc.user.id " +
           "ORDER BY commentCount DESC")
    List<Object[]> countCommentsByUser();
} 