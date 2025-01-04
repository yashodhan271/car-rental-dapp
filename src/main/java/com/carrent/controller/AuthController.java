package com.carrent.controller;

import com.carrent.model.User;
import com.carrent.security.JwtUtils;
import com.carrent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/nonce")
    public ResponseEntity<?> getNonce(@RequestParam String walletAddress) {
        String nonce = userService.generateNonce(walletAddress);
        Map<String, String> response = new HashMap<>();
        response.put("nonce", nonce);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifySignature(
            @RequestParam String walletAddress,
            @RequestParam String signature) {
        try {
            User user = userService.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new RuntimeException("User not found"));

            String nonce = user.getNonce();
            String message = "Please sign this nonce: " + nonce;

            // Verify signature
            boolean isValid = verifyEthereumSignature(message, signature, walletAddress);
            if (!isValid) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            // Generate new JWT token
            String token = jwtUtils.generateJwtToken(walletAddress);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying signature: " + e.getMessage());
        }
    }

    private boolean verifyEthereumSignature(String message, String signature, String address) {
        try {
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            byte[] r = new byte[32];
            byte[] s = new byte[32];
            System.arraycopy(signatureBytes, 0, r, 0, 32);
            System.arraycopy(signatureBytes, 32, s, 0, 32);
            byte v = signatureBytes[64];

            ECDSASignature sig = new ECDSASignature(
                new BigInteger(1, r),
                new BigInteger(1, s)
            );

            byte[] messageHash = message.getBytes();
            BigInteger publicKey = Sign.recoverFromSignature(
                v - 27,
                sig,
                messageHash
            );

            if (publicKey != null) {
                String recoveredAddress = "0x" + Keys.getAddress(publicKey);
                return recoveredAddress.equalsIgnoreCase(address);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
