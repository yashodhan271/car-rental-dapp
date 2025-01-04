package com.carrent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cars")
public class Car {
    @Id
    private String id;
    private String vinNumber;
    private String ownerAddress;
    private String make;
    private String model;
    private int year;
    private boolean isAvailable;
    private double rentalPrice;
    private String ipfsDocumentHash;
    private String gpsTrackingId;
    private String imageUrl;
}
