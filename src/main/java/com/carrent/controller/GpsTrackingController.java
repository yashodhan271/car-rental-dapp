package com.carrent.controller;

import com.carrent.model.GpsLocation;
import com.carrent.service.GpsTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GpsTrackingController {
    private final GpsTrackingService gpsTrackingService;

    @PostMapping("/{trackingId}")
    public ResponseEntity<GpsLocation> updateLocation(
            @PathVariable String trackingId,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        return ResponseEntity.ok(gpsTrackingService.updateLocation(trackingId, latitude, longitude));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<GpsLocation> getLatestLocation(@PathVariable String trackingId) {
        return gpsTrackingService.getLatestLocation(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
