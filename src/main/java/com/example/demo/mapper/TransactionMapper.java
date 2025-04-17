package com.example.demo.mapper;

import digitaldocuments.library.v1.transaction.TransactionEnums;
import digitaldocuments.library.v1.transaction.TransactionMessages;
import com.example.demo.dto.request.transaction.TransactionRequestDto;
import com.example.demo.dto.response.transaction.TransactionResponseDto;
import com.example.demo.enums.TransactionType;

public class TransactionMapper {

    public static TransactionMessages.CreateTransactionRequest toTransactionRequestDto(TransactionRequestDto requestDto) {
        Double amount = requestDto.getAmount();
        Long accountId = requestDto.getAccountId();
        TransactionEnums.TransactionType type = toProtoType(requestDto.getType());
        return  TransactionMessages.CreateTransactionRequest.newBuilder()
                .setAccountId(accountId)
                .setAmount(amount)
                .setType(type)
                .build();
    }

    public static TransactionResponseDto toTransactionResponseDto(TransactionMessages.CreateTransactionResponse response) {
        return TransactionResponseDto.builder()
                .amount(response.getAmount())
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
