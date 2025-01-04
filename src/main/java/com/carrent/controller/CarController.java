package com.carrent.controller;

import com.carrent.model.Car;
import com.carrent.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CarController {
    private final CarService carService;

    @PostMapping
    public ResponseEntity<Car> registerCar(@RequestBody Car car) {
        return ResponseEntity.ok(carService.registerCar(car));
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Car>> getAvailableCars() {
        return ResponseEntity.ok(carService.getAvailableCars());
    }

    @GetMapping("/{vinNumber}")
    public ResponseEntity<Car> getCarByVin(@PathVariable String vinNumber) {
        return carService.getCarByVin(vinNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerAddress}")
    public ResponseEntity<List<Car>> getCarsByOwner(@PathVariable String ownerAddress) {
        return ResponseEntity.ok(carService.getCarsByOwner(ownerAddress));
    }

    @PutMapping("/{vinNumber}")
    public ResponseEntity<Car> updateCar(@PathVariable String vinNumber, @RequestBody Car car) {
        if (!vinNumber.equals(car.getVinNumber())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(carService.updateCar(car));
    }
}
