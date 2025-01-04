package com.carrent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "gps_locations")
public class GpsLocation {
    @Id
    private String id;
    private String trackingId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
}
