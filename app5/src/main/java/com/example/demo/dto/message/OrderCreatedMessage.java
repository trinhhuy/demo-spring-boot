package com.example.demo.dto.message;

import com.example.demo.enums.OrderStatusEnum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderCreatedMessage {
    private Long id;

    private Long productId;

    private Double amount;

    private OrderStatusEnum status;
}
