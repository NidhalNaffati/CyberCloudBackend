package com.example.gestion_forum.repository;

import com.example.gestion_forum.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId); // ✅ Use 'IsReadFalse' because the field is 'isRead'
}
