package com.carrent.repository;

import com.carrent.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CarRepository extends MongoRepository<Car, String> {
    Optional<Car> findByVinNumber(String vinNumber);
    List<Car> findByOwnerAddress(String ownerAddress);
    List<Car> findByIsAvailable(boolean isAvailable);
}
