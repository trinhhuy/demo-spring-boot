package com.example.demo.dto.response.transaction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponseDto {
    Double amount;
}
