package com.carrent.service;

import com.carrent.model.Rental;
import com.carrent.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalService {
    private final RentalRepository rentalRepository;
    private final CarService carService;
    private final BlockchainService blockchainService;

    public Rental createRental(Rental rental) {
        // Verify car availability and create rental contract on blockchain
        // TODO: Implement blockchain rental creation
        
        // Update car availability
        carService.updateCarAvailability(rental.getVinNumber(), false);
        
        // Save rental to MongoDB
        return rentalRepository.save(rental);
    }

    public List<Rental> getRentalsByRenter(String renterAddress) {
        return rentalRepository.findByRenterAddress(renterAddress);
    }

    public Optional<Rental> getActiveRental(String vinNumber) {
        return rentalRepository.findByVinNumberAndIsActive(vinNumber, true);
    }

    public Rental completeRental(String rentalId) {
        Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            // Complete rental on blockchain
            // TODO: Implement blockchain rental completion
            
            // Update rental status
            rental.setActive(false);
            carService.updateCarAvailability(rental.getVinNumber(), true);
            return rentalRepository.save(rental);
        }
        throw new RuntimeException("Rental not found");
    }

    public List<Rental> getActiveRentals() {
        return rentalRepository.findByIsActive(true);
    }
}
