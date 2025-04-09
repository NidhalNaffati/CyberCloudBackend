package com.buzzsocial.repository;

import com.buzzsocial.model.Buzz;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuzzRepository extends JpaRepository<Buzz, String> {
    List<Buzz> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    @Query("SELECT b FROM Buzz b ORDER BY b.createdAt DESC")
    List<Buzz> findMainstream(Pageable pageable);
    
    @Query("SELECT b FROM Buzz b WHERE b.user.id IN " +
           "(SELECT uf.following.id FROM UserFollower uf WHERE uf.follower.id = :userId) " +
           "ORDER BY b.createdAt DESC")
    List<Buzz> findFollowingsBuzzs(String userId, Pageable pageable);
    
    @Query("SELECT b FROM Buzz b ORDER BY (b.viewCount + b.commentCount + b.shareCount + b.exploreCount) DESC")
    List<Buzz> findPopularBuzzs(Pageable pageable);
    
    @Query("SELECT b FROM Buzz b ORDER BY (b.viewCount + b.commentCount + b.shareCount + b.exploreCount) ASC")
    List<Buzz> findUnpopularBuzzs(Pageable pageable);
    
    @Query("SELECT b FROM Buzz b WHERE b.title LIKE %:query% OR b.content LIKE %:query%")
    List<Buzz> searchBuzzs(String query, Pageable pageable);
    
    @Query(value = "SELECT b.*, (b.view_count + b.comment_count + b.share_count + b.explore_count) as total_score " +
                  "FROM buzzs b ORDER BY total_score DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findTrendingBuzzs();
}
