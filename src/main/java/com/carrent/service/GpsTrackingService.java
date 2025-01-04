package com.carrent.service;

import com.carrent.model.GpsLocation;
import com.carrent.repository.GpsLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GpsTrackingService {
    private final GpsLocationRepository gpsLocationRepository;

    public GpsLocation updateLocation(String trackingId, double latitude, double longitude) {
        GpsLocation location = new GpsLocation();
        location.setTrackingId(trackingId);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTimestamp(LocalDateTime.now());
        
        return gpsLocationRepository.save(location);
    }

    public Optional<GpsLocation> getLatestLocation(String trackingId) {
        return gpsLocationRepository.findFirstByTrackingIdOrderByTimestampDesc(trackingId);
    }
}
