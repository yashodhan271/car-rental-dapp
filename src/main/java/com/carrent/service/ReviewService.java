package com.carrent.service;

import com.carrent.model.Review;
import com.carrent.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final NotificationService notificationService;

    public Review createReview(Review review) {
        review.setTimestamp(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        // Notify the car owner or renter about the new review
        if (review.getType() == Review.ReviewType.CAR_REVIEW) {
            notificationService.notifyNewReview(
                review.getReviewerId(),
                "your car", // TODO: Add car details
                review.getRentalId()
            );
        }

        return savedReview;
    }

    public List<Review> getCarReviews(String vinNumber) {
        return reviewRepository.findByVinNumber(vinNumber);
    }

    public List<Review> getUserReviews(String reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId);
    }

    public List<Review> getRentalReviews(String rentalId) {
        return reviewRepository.findByRentalId(rentalId);
    }

    public double getAverageRating(String vinNumber) {
        return reviewRepository.averageRatingByVinNumber(vinNumber);
    }

    public boolean hasUserReviewedRental(String reviewerId, String rentalId) {
        return reviewRepository.findByRentalId(rentalId).stream()
                .anyMatch(review -> review.getReviewerId().equals(reviewerId));
    }
}
