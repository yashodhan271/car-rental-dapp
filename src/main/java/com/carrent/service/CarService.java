package com.carrent.service;

import com.carrent.model.Car;
import com.carrent.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final BlockchainService blockchainService;

    public Car registerCar(Car car) {
        // First register on blockchain
        // TODO: Implement blockchain registration
        
        // Then save to MongoDB
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getAvailableCars() {
        return carRepository.findByIsAvailable(true);
    }

    public Optional<Car> getCarByVin(String vinNumber) {
        return carRepository.findByVinNumber(vinNumber);
    }

    public List<Car> getCarsByOwner(String ownerAddress) {
        return carRepository.findByOwnerAddress(ownerAddress);
    }

    public Car updateCar(Car car) {
        // Verify ownership on blockchain
        // TODO: Implement blockchain verification
        
        return carRepository.save(car);
    }

    public void updateCarAvailability(String vinNumber, boolean isAvailable) {
        carRepository.findByVinNumber(vinNumber).ifPresent(car -> {
            car.setAvailable(isAvailable);
            carRepository.save(car);
        });
    }
}
