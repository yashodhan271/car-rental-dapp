package com.carrent.service;

import com.carrent.model.PaymentHistory;
import com.carrent.repository.PaymentHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BlockchainService blockchainService;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void recordPayment_ShouldSaveAndNotify() {
        // Arrange
        String transactionHash = "0x123";
        String fromAddress = "0xabc";
        String toAddress = "0xdef";
        BigDecimal amount = new BigDecimal("1.5");
        String rentalId = "rental123";
        PaymentHistory.PaymentType type = PaymentHistory.PaymentType.RENTAL_PAYMENT;
        String currency = "ETH";

        PaymentHistory expectedPayment = new PaymentHistory();
        expectedPayment.setTransactionHash(transactionHash);
        expectedPayment.setFromAddress(fromAddress);
        expectedPayment.setToAddress(toAddress);
        expectedPayment.setAmount(amount);
        expectedPayment.setRentalId(rentalId);
        expectedPayment.setType(type);
        expectedPayment.setStatus(PaymentHistory.PaymentStatus.COMPLETED);
        expectedPayment.setCurrency(currency);

        when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenReturn(expectedPayment);

        // Act
        PaymentHistory result = paymentService.recordPayment(
            transactionHash, fromAddress, toAddress, amount, rentalId, type, currency
        );

        // Assert
        assertNotNull(result);
        assertEquals(transactionHash, result.getTransactionHash());
        assertEquals(amount, result.getAmount());
        assertEquals(PaymentHistory.PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(notificationService, times(1)).notifyPaymentReceived(
            eq(toAddress),
            eq(amount.toString()),
            eq(rentalId)
        );
    }

    @Test
    void getUserPayments_ShouldReturnAllPayments() {
        // Arrange
        String address = "0x123";
        List<PaymentHistory> sentPayments = new ArrayList<>(Arrays.asList(
            createPayment("1", LocalDateTime.now()),
            createPayment("2", LocalDateTime.now().minusHours(1))
        ));
        List<PaymentHistory> receivedPayments = new ArrayList<>(Arrays.asList(
            createPayment("3", LocalDateTime.now().minusHours(2))
        ));

        when(paymentHistoryRepository.findByFromAddressOrderByTimestampDesc(address))
            .thenReturn(sentPayments);
        when(paymentHistoryRepository.findByToAddressOrderByTimestampDesc(address))
            .thenReturn(receivedPayments);

        // Act
        List<PaymentHistory> result = paymentService.getUserPayments(address);

        // Assert
        assertEquals(3, result.size());
        verify(paymentHistoryRepository, times(1))
            .findByFromAddressOrderByTimestampDesc(address);
        verify(paymentHistoryRepository, times(1))
            .findByToAddressOrderByTimestampDesc(address);
    }

    @Test
    void calculateTotalEarnings_ShouldReturnCorrectSum() {
        // Arrange
        String address = "0x123";
        List<PaymentHistory> receivedPayments = Arrays.asList(
            createPaymentWithAmount("1", new BigDecimal("1.5")),
            createPaymentWithAmount("2", new BigDecimal("2.5"))
        );

        when(paymentHistoryRepository.findByToAddressOrderByTimestampDesc(address))
            .thenReturn(receivedPayments);

        // Act
        BigDecimal result = paymentService.calculateTotalEarnings(address);

        // Assert
        assertEquals(new BigDecimal("4.0"), result);
        verify(paymentHistoryRepository, times(1))
            .findByToAddressOrderByTimestampDesc(address);
    }

    private PaymentHistory createPayment(String id, LocalDateTime timestamp) {
        PaymentHistory payment = new PaymentHistory();
        payment.setId(id);
        payment.setTimestamp(timestamp);
        payment.setStatus(PaymentHistory.PaymentStatus.COMPLETED);
        payment.setAmount(BigDecimal.ONE);
        return payment;
    }

    private PaymentHistory createPaymentWithAmount(String id, BigDecimal amount) {
        PaymentHistory payment = createPayment(id, LocalDateTime.now());
        payment.setAmount(amount);
        return payment;
    }
}
