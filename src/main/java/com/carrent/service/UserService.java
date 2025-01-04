package com.carrent.service;

import com.carrent.model.Role;
import com.carrent.model.User;
import com.carrent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String walletAddress) throws UsernameNotFoundException {
        User user = userRepository.findByWalletAddress(walletAddress)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with wallet address: " + walletAddress));

        return org.springframework.security.core.userdetails.User
            .withUsername(walletAddress)
            .password("") // No password for wallet authentication
            .authorities(getAuthorities(user.getRoles()))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.isEnabled())
            .build();
    }

    public UserDetails loadUserByWalletAddress(String walletAddress) {
        return loadUserByUsername(walletAddress);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toList());
    }

    public Optional<User> findByWalletAddress(String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress);
    }

    public User createUser(String walletAddress, String username, String email) {
        if (userRepository.existsByWalletAddress(walletAddress)) {
            throw new RuntimeException("Wallet address already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setWalletAddress(walletAddress);
        user.setUsername(username);
        user.setEmail(email);
        user.setRoles(Set.of(Role.ROLE_USER));
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public void addRole(String walletAddress, Role role) {
        User user = userRepository.findByWalletAddress(walletAddress)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<Role> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);
        
        userRepository.save(user);
    }

    public void removeRole(String walletAddress, Role role) {
        User user = userRepository.findByWalletAddress(walletAddress)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<Role> roles = user.getRoles();
        roles.remove(role);
        user.setRoles(roles);
        
        userRepository.save(user);
    }

    public String generateNonce(String walletAddress) {
        User user = userRepository.findByWalletAddress(walletAddress)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setWalletAddress(walletAddress);
                newUser.setEnabled(true);
                newUser.setRoles(Set.of(Role.ROLE_USER));
                return newUser;
            });

        String nonce = generateRandomNonce();
        user.setNonce(nonce);
        userRepository.save(user);

        return nonce;
    }

    private String generateRandomNonce() {
        return String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 1000000));
    }
}
