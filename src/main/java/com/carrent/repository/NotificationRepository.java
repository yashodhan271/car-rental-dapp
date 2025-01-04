package com.carrent.repository;

import com.carrent.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByTimestampDesc(String userId);
    List<Notification> findByUserIdAndReadOrderByTimestampDesc(String userId, boolean read);
    long countByUserIdAndRead(String userId, boolean read);
}
