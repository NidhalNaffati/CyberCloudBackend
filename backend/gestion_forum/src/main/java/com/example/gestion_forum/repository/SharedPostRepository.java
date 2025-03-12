package com.example.gestion_forum.repository;

import com.example.gestion_forum.models.SharedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SharedPostRepository extends JpaRepository<SharedPost, Long> {
    List<SharedPost> findByUserId(Long userId);
}
