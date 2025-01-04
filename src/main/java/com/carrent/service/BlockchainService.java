package com.carrent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class BlockchainService {
    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;
    private final String contractAddress;

    public BlockchainService(
            @Value("${ethereum.network.url}") String networkUrl,
            @Value("${ethereum.wallet.private-key}") String privateKey,
            @Value("${ethereum.contract.address}") String contractAddress
    ) {
        this.web3j = Web3j.build(new HttpService(networkUrl));
        this.credentials = Credentials.create(privateKey);
        this.gasProvider = new StaticGasProvider(
            BigInteger.valueOf(20000000000L), // 20 Gwei
            BigInteger.valueOf(6721975L) // Gas Limit
        );
        this.contractAddress = contractAddress;
    }

    public CompletableFuture<TransactionReceipt> registerCar(
            String vinNumber,
            String make,
            String model,
            BigInteger year,
            BigInteger rentalPrice,
            String ipfsDocumentHash
    ) {
        Function function = new Function(
            "registerCar",
            Arrays.asList(
                new org.web3j.abi.datatypes.Utf8String(vinNumber),
                new org.web3j.abi.datatypes.Utf8String(make),
                new org.web3j.abi.datatypes.Utf8String(model),
                new org.web3j.abi.datatypes.generated.Uint256(year),
                new org.web3j.abi.datatypes.generated.Uint256(rentalPrice),
                new org.web3j.abi.datatypes.Utf8String(ipfsDocumentHash)
            ),
            Collections.emptyList()
        );

        return sendTransaction(function);
    }

    public CompletableFuture<TransactionReceipt> rentCar(
            String vinNumber,
            BigInteger durationInDays,
            String gpsTrackingId,
            BigInteger value
    ) {
        Function function = new Function(
            "rentCar",
            Arrays.asList(
                new org.web3j.abi.datatypes.Utf8String(vinNumber),
                new org.web3j.abi.datatypes.generated.Uint256(durationInDays),
                new org.web3j.abi.datatypes.Utf8String(gpsTrackingId)
            ),
            Collections.emptyList()
        );

        return sendTransaction(function, value);
    }

    public CompletableFuture<TransactionReceipt> completeRental(String vinNumber) {
        Function function = new Function(
            "completeRental",
            Arrays.asList(new org.web3j.abi.datatypes.Utf8String(vinNumber)),
            Collections.emptyList()
        );

        return sendTransaction(function);
    }

    public CompletableFuture<TransactionReceipt> transferOwnership(
            String vinNumber,
            String newOwner
    ) {
        Function function = new Function(
            "transferOwnership",
            Arrays.asList(
                new org.web3j.abi.datatypes.Utf8String(vinNumber),
                new org.web3j.abi.datatypes.Address(newOwner)
            ),
            Collections.emptyList()
        );

        return sendTransaction(function);
    }

    private CompletableFuture<TransactionReceipt> sendTransaction(Function function) {
        return sendTransaction(function, BigInteger.ZERO);
    }

    private CompletableFuture<TransactionReceipt> sendTransaction(Function function, BigInteger value) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String encodedFunction = org.web3j.abi.FunctionEncoder.encode(function);
                
                org.web3j.protocol.core.methods.request.Transaction transaction = 
                    org.web3j.protocol.core.methods.request.Transaction.createFunctionCallTransaction(
                        credentials.getAddress(),
                        null,
                        gasProvider.getGasPrice(),
                        gasProvider.getGasLimit(),
                        contractAddress,
                        value,
                        encodedFunction
                    );

                return web3j.ethSendTransaction(transaction)
                    .send()
                    .getTransactionHash();
            } catch (Exception e) {
                log.error("Error sending transaction", e);
                throw new RuntimeException("Failed to send transaction", e);
            }
        }).thenCompose(txHash -> 
            CompletableFuture.supplyAsync(() -> {
                try {
                    return web3j.ethGetTransactionReceipt(txHash)
                        .send()
                        .getTransactionReceipt()
                        .orElseThrow(() -> new RuntimeException("Transaction receipt not found"));
                } catch (Exception e) {
                    log.error("Error getting transaction receipt", e);
                    throw new RuntimeException("Failed to get transaction receipt", e);
                }
            })
        );
    }
}
