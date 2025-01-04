package com.carrent.controller;

import com.carrent.model.Review;
import com.carrent.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    @GetMapping("/car/{vinNumber}")
    public ResponseEntity<List<Review>> getCarReviews(@PathVariable String vinNumber) {
        return ResponseEntity.ok(reviewService.getCarReviews(vinNumber));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<Review>> getRentalReviews(@PathVariable String rentalId) {
        return ResponseEntity.ok(reviewService.getRentalReviews(rentalId));
    }

    @GetMapping("/car/{vinNumber}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable String vinNumber) {
        return ResponseEntity.ok(reviewService.getAverageRating(vinNumber));
    }

    @GetMapping("/check/{rentalId}/{userId}")
    public ResponseEntity<Boolean> hasUserReviewedRental(
            @PathVariable String rentalId,
            @PathVariable String userId) {
        return ResponseEntity.ok(reviewService.hasUserReviewedRental(userId, rentalId));
    }
}
