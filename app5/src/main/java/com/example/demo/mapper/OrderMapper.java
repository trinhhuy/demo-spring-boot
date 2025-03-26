package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.CreateAOrderRequest;
import com.example.demo.GetAOrderResponse;
import com.example.demo.dto.message.OrderCreatedMessage;
import com.example.demo.models.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "status", target = "orderStatus")
    GetAOrderResponse toGetAOrderResponse(Order order);

    Order toOrder(CreateAOrderRequest request);

    OrderCreatedMessage toOrderCreatedMessage(Order order);
}
