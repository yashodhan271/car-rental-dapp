package com.carrent.repository;

import com.carrent.model.Rental;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends MongoRepository<Rental, String> {
    List<Rental> findByRenterAddress(String renterAddress);
    Optional<Rental> findByVinNumberAndIsActive(String vinNumber, boolean isActive);
    List<Rental> findByIsActive(boolean isActive);
}
