package com.carrent.service;

import com.carrent.model.Notification;
import com.carrent.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification createNotification(String userId, String title, String message, 
            Notification.NotificationType type, String referenceId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndReadOrderByTimestampDesc(userId, false);
    }

    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }

    // Notification templates
    public void notifyRentalCreated(String userId, String rentalId, String carDetails) {
        createNotification(
            userId,
            "New Rental Created",
            "Your rental for " + carDetails + " has been confirmed.",
            Notification.NotificationType.RENTAL_CREATED,
            rentalId
        );
    }

    public void notifyRentalCompleted(String userId, String rentalId, String carDetails) {
        createNotification(
            userId,
            "Rental Completed",
            "Your rental for " + carDetails + " has been completed.",
            Notification.NotificationType.RENTAL_COMPLETED,
            rentalId
        );
    }

    public void notifyPaymentReceived(String userId, String amount, String rentalId) {
        createNotification(
            userId,
            "Payment Received",
            "You have received a payment of " + amount + " ETH.",
            Notification.NotificationType.PAYMENT_RECEIVED,
            rentalId
        );
    }

    public void notifyNewReview(String userId, String carDetails, String rentalId) {
        createNotification(
            userId,
            "New Review Received",
            "You have received a new review for " + carDetails,
            Notification.NotificationType.NEW_REVIEW,
            rentalId
        );
    }

    public void notifyRentalReminder(String userId, String rentalId, String carDetails) {
        createNotification(
            userId,
            "Rental Reminder",
            "Your rental for " + carDetails + " is ending soon.",
            Notification.NotificationType.RENTAL_REMINDER,
            rentalId
        );
    }

    public void notifyGpsAlert(String userId, String rentalId, String message) {
        createNotification(
            userId,
            "GPS Alert",
            message,
            Notification.NotificationType.GPS_ALERT,
            rentalId
        );
    }
}
