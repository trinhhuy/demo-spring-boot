package com.example.demo.dto.response;

import com.example.demo.enums.OrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class OrderResponse {

    private long id;

    private double amount;

    private OrderStatusEnum orderStatus;

}