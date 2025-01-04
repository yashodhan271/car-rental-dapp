package com.carrent.service;

import com.carrent.model.Review;
import com.carrent.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReview_ShouldSaveAndNotify() {
        // Arrange
        Review review = new Review();
        review.setVinNumber("VIN123");
        review.setReviewerId("user123");
        review.setRentalId("rental123");
        review.setRating(5);
        review.setComment("Great car!");
        review.setType(Review.ReviewType.CAR_REVIEW);

        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        Review result = reviewService.createReview(review);

        // Assert
        assertNotNull(result);
        assertEquals(review.getVinNumber(), result.getVinNumber());
        assertEquals(review.getRating(), result.getRating());
        assertNotNull(result.getTimestamp());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(notificationService, times(1)).notifyNewReview(
            eq(review.getReviewerId()),
            anyString(),
            eq(review.getRentalId())
        );
    }

    @Test
    void getCarReviews_ShouldReturnReviews() {
        // Arrange
        String vinNumber = "VIN123";
        List<Review> expectedReviews = Arrays.asList(
            new Review(),
            new Review()
        );
        when(reviewRepository.findByVinNumber(vinNumber)).thenReturn(expectedReviews);

        // Act
        List<Review> result = reviewService.getCarReviews(vinNumber);

        // Assert
        assertEquals(expectedReviews.size(), result.size());
        verify(reviewRepository, times(1)).findByVinNumber(vinNumber);
    }

    @Test
    void getUserReviews_ShouldReturnReviews() {
        // Arrange
        String reviewerId = "user123";
        List<Review> expectedReviews = Arrays.asList(
            new Review(),
            new Review()
        );
        when(reviewRepository.findByReviewerId(reviewerId)).thenReturn(expectedReviews);

        // Act
        List<Review> result = reviewService.getUserReviews(reviewerId);

        // Assert
        assertEquals(expectedReviews.size(), result.size());
        verify(reviewRepository, times(1)).findByReviewerId(reviewerId);
    }

    @Test
    void getAverageRating_ShouldReturnCorrectAverage() {
        // Arrange
        String vinNumber = "VIN123";
        double expectedRating = 4.5;
        when(reviewRepository.averageRatingByVinNumber(vinNumber)).thenReturn(expectedRating);

        // Act
        double result = reviewService.getAverageRating(vinNumber);

        // Assert
        assertEquals(expectedRating, result);
        verify(reviewRepository, times(1)).averageRatingByVinNumber(vinNumber);
    }

    @Test
    void hasUserReviewedRental_ShouldReturnTrue_WhenReviewExists() {
        // Arrange
        String reviewerId = "user123";
        String rentalId = "rental123";
        Review review = new Review();
        review.setReviewerId(reviewerId);
        when(reviewRepository.findByRentalId(rentalId))
            .thenReturn(Arrays.asList(review));

        // Act
        boolean result = reviewService.hasUserReviewedRental(reviewerId, rentalId);

        // Assert
        assertTrue(result);
        verify(reviewRepository, times(1)).findByRentalId(rentalId);
    }
}
