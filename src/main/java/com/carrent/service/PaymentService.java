package com.carrent.service;

import com.carrent.model.PaymentHistory;
import com.carrent.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final NotificationService notificationService;
    private final BlockchainService blockchainService;

    public PaymentHistory recordPayment(
            String transactionHash,
            String fromAddress,
            String toAddress,
            BigDecimal amount,
            String rentalId,
            PaymentHistory.PaymentType type,
            String currency
    ) {
        PaymentHistory payment = new PaymentHistory();
        payment.setTransactionHash(transactionHash);
        payment.setFromAddress(fromAddress);
        payment.setToAddress(toAddress);
        payment.setAmount(amount);
        payment.setRentalId(rentalId);
        payment.setType(type);
        payment.setStatus(PaymentHistory.PaymentStatus.COMPLETED);
        payment.setTimestamp(LocalDateTime.now());
        payment.setCurrency(currency);

        PaymentHistory savedPayment = paymentHistoryRepository.save(payment);

        // Notify both parties
        notificationService.notifyPaymentReceived(
            toAddress,
            amount.toString(),
            rentalId
        );

        return savedPayment;
    }

    public List<PaymentHistory> getUserPayments(String address) {
        List<PaymentHistory> sentPayments = 
            paymentHistoryRepository.findByFromAddressOrderByTimestampDesc(address);
        List<PaymentHistory> receivedPayments = 
            paymentHistoryRepository.findByToAddressOrderByTimestampDesc(address);
        
        // Create a new modifiable list and add all payments
        List<PaymentHistory> allPayments = new ArrayList<>(sentPayments);
        allPayments.addAll(receivedPayments);
        allPayments.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        
        return allPayments;
    }

    public List<PaymentHistory> getRentalPayments(String rentalId) {
        return paymentHistoryRepository.findByRentalId(rentalId);
    }

    public PaymentHistory getPaymentByTransactionHash(String transactionHash) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByTransactionHash(transactionHash);
        return payments.isEmpty() ? null : payments.get(0);
    }

    public BigDecimal calculateTotalEarnings(String address) {
        List<PaymentHistory> receivedPayments = 
            paymentHistoryRepository.findByToAddressOrderByTimestampDesc(address);
        
        return receivedPayments.stream()
                .filter(p -> p.getStatus() == PaymentHistory.PaymentStatus.COMPLETED)
                .map(PaymentHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalSpent(String address) {
        List<PaymentHistory> sentPayments = 
            paymentHistoryRepository.findByFromAddressOrderByTimestampDesc(address);
        
        return sentPayments.stream()
                .filter(p -> p.getStatus() == PaymentHistory.PaymentStatus.COMPLETED)
                .map(PaymentHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
