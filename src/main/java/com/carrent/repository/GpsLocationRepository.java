package com.carrent.repository;

import com.carrent.model.GpsLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface GpsLocationRepository extends MongoRepository<GpsLocation, String> {
    Optional<GpsLocation> findFirstByTrackingIdOrderByTimestampDesc(String trackingId);
}
