package tn.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.BlogPost;

import java.util.Date;
import java.util.List;

@Repository
public interface StatisticsBlogRepository extends JpaRepository<BlogPost, Long> {
    
    // Requête pour compter les publications par mois
    @Query("SELECT MONTH(bp.createdAt) as month, YEAR(bp.createdAt) as year, COUNT(bp) as count " +
           "FROM BlogPost bp " +
           "WHERE (:startDate IS NULL OR bp.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR bp.createdAt <= :endDate) " +
           "GROUP BY YEAR(bp.createdAt), MONTH(bp.createdAt) " +
           "ORDER BY year, month")
    List<Object[]> countPostsByMonth(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Requête pour compter les publications par jour
    @Query("SELECT DAY(bp.createdAt) as day, MONTH(bp.createdAt) as month, YEAR(bp.createdAt) as year, COUNT(bp) as count " +
           "FROM BlogPost bp " +
           "WHERE (:startDate IS NULL OR bp.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR bp.createdAt <= :endDate) " +
           "GROUP BY YEAR(bp.createdAt), MONTH(bp.createdAt), DAY(bp.createdAt) " +
           "ORDER BY year, month, day")
    List<Object[]> countPostsByDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Requête pour compter les publications par année
    @Query("SELECT YEAR(bp.createdAt) as year, COUNT(bp) as count " +
           "FROM BlogPost bp " +
           "WHERE (:startDate IS NULL OR bp.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR bp.createdAt <= :endDate) " +
           "GROUP BY YEAR(bp.createdAt) " +
           "ORDER BY year")
    List<Object[]> countPostsByYear(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Requête pour obtenir les publications les plus commentées
    @Query("SELECT bp.postId as postId, SIZE(bp.comments) as commentCount " +
           "FROM BlogPost bp " +
           "GROUP BY bp.postId " +
           "ORDER BY commentCount DESC")
    List<Object[]> findMostCommentedPosts();
    
    // Requête pour compter les réactions par type
    @Query("SELECT bp.reaction as reactionType, COUNT(bp) as count " +
           "FROM BlogPost bp " +
           "WHERE bp.reaction IS NOT NULL " +
           "GROUP BY bp.reaction")
    List<Object[]> countReactionsByType();
} 