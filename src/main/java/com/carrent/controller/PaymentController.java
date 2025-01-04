package com.carrent.controller;

import com.carrent.model.PaymentHistory;
import com.carrent.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/user/{address}")
    public ResponseEntity<List<PaymentHistory>> getUserPayments(@PathVariable String address) {
        return ResponseEntity.ok(paymentService.getUserPayments(address));
    }

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<PaymentHistory>> getRentalPayments(@PathVariable String rentalId) {
        return ResponseEntity.ok(paymentService.getRentalPayments(rentalId));
    }

    @GetMapping("/transaction/{hash}")
    public ResponseEntity<PaymentHistory> getPaymentByTransactionHash(@PathVariable String hash) {
        PaymentHistory payment = paymentService.getPaymentByTransactionHash(hash);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{address}/earnings")
    public ResponseEntity<BigDecimal> getTotalEarnings(@PathVariable String address) {
        return ResponseEntity.ok(paymentService.calculateTotalEarnings(address));
    }

    @GetMapping("/user/{address}/spent")
    public ResponseEntity<BigDecimal> getTotalSpent(@PathVariable String address) {
        return ResponseEntity.ok(paymentService.calculateTotalSpent(address));
    }
}
