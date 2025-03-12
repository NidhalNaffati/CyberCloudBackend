package com.example.gestion_forum.services;

import com.example.gestion_forum.models.Notification;
import com.example.gestion_forum.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;

    /**
     * Get all unread notifications for a user.
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        logger.info("Fetching unread notifications for user ID: {}", userId);
        return notificationRepository.findByUserIdAndIsReadFalse(userId); // ✅ Use 'IsReadFalse' because the entity field is 'isRead'
    }


    /**
     * ✅ Fix: Add `findById` method to fetch a notification by ID.
     */
    public Optional<Notification> findById(Long notificationId) {
        logger.info("Fetching notification with ID: {}", notificationId);
        return notificationRepository.findById(notificationId);
    }

    /**
     * Send a new notification.
     */
    public Notification sendNotification(Notification notification) {
        logger.info("Sending notification: {}", notification.getMessage());
        return notificationRepository.save(notification);
    }

    /**
     * Mark a single notification as read.
     */
    public void markAsRead(Long notificationId) {
        logger.info("Marking notification ID {} as read", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * ✅ New Feature: Mark multiple notifications as read at once.
     */
    public void markMultipleAsRead(List<Long> notificationIds) {
        logger.info("Marking multiple notifications as read: {}", notificationIds);
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);

        if (notifications.isEmpty()) {
            throw new RuntimeException("No notifications found for the provided IDs.");
        }

        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}
