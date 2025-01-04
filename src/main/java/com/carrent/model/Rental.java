package com.carrent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "rentals")
public class Rental {
    @Id
    private String id;
    private String vinNumber;
    private String renterAddress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalAmount;
    private boolean isActive;
    private String gpsTrackingId;
    private String transactionHash;
}
