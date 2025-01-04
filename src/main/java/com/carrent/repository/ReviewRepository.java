package com.carrent.repository;

import com.carrent.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByVinNumber(String vinNumber);
    List<Review> findByReviewerId(String reviewerId);
    List<Review> findByRentalId(String rentalId);
    double averageRatingByVinNumber(String vinNumber);
}
