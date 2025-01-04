package com.carrent.service;

import com.carrent.model.Notification;
import com.carrent.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNotification_ShouldSaveNotification() {
        // Arrange
        String userId = "user123";
        String title = "Test Title";
        String message = "Test Message";
        Notification.NotificationType type = Notification.NotificationType.RENTAL_CREATED;
        String referenceId = "rental123";

        Notification expectedNotification = new Notification();
        expectedNotification.setId("notif123");
        expectedNotification.setUserId(userId);
        expectedNotification.setTitle(title);
        expectedNotification.setMessage(message);
        expectedNotification.setType(type);
        expectedNotification.setReferenceId(referenceId);
        expectedNotification.setTimestamp(LocalDateTime.now());
        expectedNotification.setRead(false);

        when(notificationRepository.save(any(Notification.class))).thenReturn(expectedNotification);

        // Act
        Notification result = notificationService.createNotification(userId, title, message, type, referenceId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(title, result.getTitle());
        assertEquals(message, result.getMessage());
        assertEquals(type, result.getType());
        assertEquals(referenceId, result.getReferenceId());
        assertFalse(result.isRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getUserNotifications_ShouldReturnUserNotifications() {
        // Arrange
        String userId = "user123";
        List<Notification> expectedNotifications = Arrays.asList(
            new Notification(),
            new Notification()
        );
        when(notificationRepository.findByUserIdOrderByTimestampDesc(userId))
            .thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getUserNotifications(userId);

        // Assert
        assertEquals(expectedNotifications.size(), result.size());
        verify(notificationRepository, times(1)).findByUserIdOrderByTimestampDesc(userId);
    }

    @Test
    void markAsRead_ShouldUpdateNotificationStatus() {
        // Arrange
        String notificationId = "notif123";
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setRead(false);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getUnreadCount_ShouldReturnCorrectCount() {
        // Arrange
        String userId = "user123";
        long expectedCount = 5;
        when(notificationRepository.countByUserIdAndRead(userId, false)).thenReturn(expectedCount);

        // Act
        long result = notificationService.getUnreadCount(userId);

        // Assert
        assertEquals(expectedCount, result);
        verify(notificationRepository, times(1)).countByUserIdAndRead(userId, false);
    }
}
