package com.buzzsocial.repository;

import com.buzzsocial.model.BuzzComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuzzCommentRepository extends JpaRepository<BuzzComment, String> {
    List<BuzzComment> findByBuzzIdOrderByCreatedAtDesc(String buzzId, Pageable pageable);
    
    long countByBuzzId(String buzzId);
    
    List<BuzzComment> findByContentContainingIgnoreCase(String query);
}
