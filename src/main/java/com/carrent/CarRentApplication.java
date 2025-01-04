package com.carrent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class CarRentApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarRentApplication.class, args);
    }
}
