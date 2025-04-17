package com.example.demo.adapter.grpc.client;

import com.vtbank.digitaldocuments.library.v1.TransactionServiceGrpc;
import digitaldocuments.library.v1.transaction.TransactionMessages;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class TransactionGrpcClient {

    @GrpcClient("transaction-service")
    private TransactionServiceGrpc.TransactionServiceBlockingStub transactionServiceBlockingStub;

    public TransactionMessages.CreateTransactionResponse createTransaction(TransactionMessages.CreateTransactionRequest request) {
        return transactionServiceBlockingStub.createTransaction(request);
    }

}
