package com.example.demo.dto.message.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceUpdatedMessageDto {
    private Long transactionId;
}
