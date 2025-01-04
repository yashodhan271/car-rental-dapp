package com.carrent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BlockchainServiceTest {

    @Mock
    private Web3j web3j;

    private BlockchainService blockchainService;
    private static final String NETWORK_URL = "http://localhost:8545";
    private static final String PRIVATE_KEY = "0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef";
    private static final String CONTRACT_ADDRESS = "0x1234567890123456789012345678901234567890";

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Mock transaction response
        EthSendTransaction ethSendTransaction = mock(EthSendTransaction.class);
        when(ethSendTransaction.getTransactionHash()).thenReturn("0xHash123");
        Request<?, EthSendTransaction> sendRequest = mock(Request.class);
        when(sendRequest.send()).thenReturn(ethSendTransaction);
        when(web3j.ethSendTransaction(any())).thenReturn(sendRequest);

        // Mock receipt response
        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.isStatusOK()).thenReturn(true);
        EthGetTransactionReceipt ethGetReceipt = mock(EthGetTransactionReceipt.class);
        when(ethGetReceipt.getTransactionReceipt()).thenReturn(Optional.of(receipt));
        Request<?, EthGetTransactionReceipt> receiptRequest = mock(Request.class);
        when(receiptRequest.send()).thenReturn(ethGetReceipt);
        when(web3j.ethGetTransactionReceipt(anyString())).thenReturn(receiptRequest);

        blockchainService = new BlockchainService(NETWORK_URL, PRIVATE_KEY, CONTRACT_ADDRESS);
    }

    @Test
    void registerCar_Success() throws Exception {
        // Arrange
        String vinNumber = "VIN123";
        String make = "Toyota";
        String model = "Camry";
        BigInteger year = BigInteger.valueOf(2023);
        BigInteger rentalPrice = BigInteger.valueOf(1000000000000000000L); // 1 ETH
        String ipfsHash = "QmHash123";

        // Act
        CompletableFuture<TransactionReceipt> result = blockchainService.registerCar(
            vinNumber, make, model, year, rentalPrice, ipfsHash
        );

        // Assert
        TransactionReceipt actualReceipt = result.join();
        assertTrue(actualReceipt.isStatusOK());
        verify(web3j).ethSendTransaction(any());
        verify(web3j).ethGetTransactionReceipt(anyString());
    }

    @Test
    void rentCar_Success() throws Exception {
        // Arrange
        String vinNumber = "VIN123";
        BigInteger durationInDays = BigInteger.valueOf(3);
        String gpsTrackingId = "GPS123";
        BigInteger value = BigInteger.valueOf(3000000000000000000L); // 3 ETH

        // Act
        CompletableFuture<TransactionReceipt> result = blockchainService.rentCar(
            vinNumber, durationInDays, gpsTrackingId, value
        );

        // Assert
        TransactionReceipt actualReceipt = result.join();
        assertTrue(actualReceipt.isStatusOK());
        verify(web3j).ethSendTransaction(any());
        verify(web3j).ethGetTransactionReceipt(anyString());
    }

    @Test
    void completeRental_Success() throws Exception {
        // Arrange
        String vinNumber = "VIN123";

        // Act
        CompletableFuture<TransactionReceipt> result = blockchainService.completeRental(vinNumber);

        // Assert
        TransactionReceipt actualReceipt = result.join();
        assertTrue(actualReceipt.isStatusOK());
        verify(web3j).ethSendTransaction(any());
        verify(web3j).ethGetTransactionReceipt(anyString());
    }
}
