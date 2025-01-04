package com.carrent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "payment_history")
public class PaymentHistory {
    @Id
    private String id;
    private String transactionHash;
    private String fromAddress;
    private String toAddress;
    private BigDecimal amount;
    private String rentalId;
    private PaymentType type;
    private PaymentStatus status;
    private LocalDateTime timestamp;
    private String currency; // ETH, USDC, etc.

    public enum PaymentType {
        RENTAL_PAYMENT,
        SECURITY_DEPOSIT,
        REFUND,
        PENALTY
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}
