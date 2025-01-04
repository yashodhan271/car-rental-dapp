package com.carrent.repository;

import com.carrent.model.PaymentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PaymentHistoryRepository extends MongoRepository<PaymentHistory, String> {
    List<PaymentHistory> findByFromAddressOrderByTimestampDesc(String fromAddress);
    List<PaymentHistory> findByToAddressOrderByTimestampDesc(String toAddress);
    List<PaymentHistory> findByRentalId(String rentalId);
    List<PaymentHistory> findByTransactionHash(String transactionHash);
}
