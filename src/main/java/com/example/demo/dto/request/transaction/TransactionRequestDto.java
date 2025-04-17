package com.example.demo.dto.request.transaction;

import com.example.demo.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequestDto {
    @NotNull(message = "Account ID cannot be null")
    @Min(value = 1, message = "Account ID must be greater than 0")
    private Long accountId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Transaction type cannot be null")
    private TransactionType type;
}
