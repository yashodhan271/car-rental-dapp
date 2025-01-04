package com.carrent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String walletAddress;
    private String username;
    private String email;
    private Set<Role> roles;
    private boolean isEnabled;
    private String nonce; // For wallet authentication
}
