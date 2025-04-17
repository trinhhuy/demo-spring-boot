package com.example.demo.mapper;

import com.example.demo.dto.message.TransactionInitiatedMessageDto;
import com.example.demo.enums.TransactionType;
import com.example.demo.models.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import digitaldocuments.library.v1.transaction.TransactionEnums;
import digitaldocuments.library.v1.transaction.TransactionMessages;

public class TransactionMapper {
    // message
    public static String toMessage(Transaction transaction) {
        try {
            TransactionInitiatedMessageDto transactionMessage = TransactionInitiatedMessageDto.builder()
                    .transactionId(transaction.getId())
                    .accountId(transaction.getAccountId())
                    .amount(transaction.getAmount())
                    .type(transaction.getType())
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(transactionMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to " + e.getMessage(), e);
        }
    }

    public static <T> T parseMessage(String jsonStrMessage, TypeReference<T> typeReference) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonStrMessage, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse message", e);
        }
    }

    // response
    public static TransactionMessages.CreateTransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionMessages.CreateTransactionResponse.newBuilder()
                .setId(transaction.getId())
                .setAccountId(transaction.getAccountId())
                .setAmount(transaction.getAmount())
                .setType(toProtoType(transaction.getType()))
                .build();
    }

    public static TransactionType toDomainType(TransactionEnums.TransactionType protoType) {
        if (protoType == TransactionEnums.TransactionType.UNKNOWN) {
            throw new IllegalArgumentException("Transaction type cannot be UNKNOWN");
        }
        return TransactionType.valueOf(protoType.name());
    }

    public static TransactionEnums.TransactionType toProtoType(TransactionType domainType) {
        return TransactionEnums.TransactionType.valueOf(domainType.name());
    }
}
