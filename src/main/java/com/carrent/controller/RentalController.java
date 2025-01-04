package com.carrent.controller;

import com.carrent.model.Rental;
import com.carrent.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental) {
        return ResponseEntity.ok(rentalService.createRental(rental));
    }

    @GetMapping("/renter/{renterAddress}")
    public ResponseEntity<List<Rental>> getRentalsByRenter(@PathVariable String renterAddress) {
        return ResponseEntity.ok(rentalService.getRentalsByRenter(renterAddress));
    }

    @GetMapping("/active/{vinNumber}")
    public ResponseEntity<Rental> getActiveRental(@PathVariable String vinNumber) {
        return rentalService.getActiveRental(vinNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{rentalId}/complete")
    public ResponseEntity<Rental> completeRental(@PathVariable String rentalId) {
        return ResponseEntity.ok(rentalService.completeRental(rentalId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Rental>> getActiveRentals() {
        return ResponseEntity.ok(rentalService.getActiveRentals());
    }
}
