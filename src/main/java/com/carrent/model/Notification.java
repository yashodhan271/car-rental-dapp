package com.carrent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String title;
    private String message;
    private NotificationType type;
    private String referenceId; // Can be rentalId, reviewId, etc.
    private LocalDateTime timestamp;
    private boolean read;

    public enum NotificationType {
        RENTAL_CREATED,
        RENTAL_COMPLETED,
        PAYMENT_RECEIVED,
        PAYMENT_SENT,
        NEW_REVIEW,
        RENTAL_REMINDER,
        GPS_ALERT
    }
}
