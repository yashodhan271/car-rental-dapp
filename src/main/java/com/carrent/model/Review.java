package com.carrent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String vinNumber;
    private String reviewerId;
    private String rentalId;
    private int rating;
    private String comment;
    private LocalDateTime timestamp;
    private ReviewType type;

    public enum ReviewType {
        CAR_REVIEW,      // Review for the car
        RENTER_REVIEW    // Review for the renter
    }
}
