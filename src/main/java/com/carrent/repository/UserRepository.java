package com.carrent.repository;

import com.carrent.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByWalletAddress(String walletAddress);
    boolean existsByWalletAddress(String walletAddress);
    boolean existsByEmail(String email);
}
