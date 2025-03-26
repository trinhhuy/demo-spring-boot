package com.example.demo.services;


import com.example.demo.dto.request.OrderRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.enums.OrderStatusEnum;
import org.springframework.stereotype.Service;
import com.example.demo.*;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class App5ServiceImpl {

    GrpcClientServiceImpl grpcClientService;

    public App5ServiceImpl(GrpcClientServiceImpl grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    public OrderResponse getOrder(long id) {
        OrderServiceGrpc.OrderServiceBlockingStub stub = grpcClientService.getOrderServiceStub();
        // Gọi các method từ stub
        GetAOrderRequest orderRequest =
                GetAOrderRequest.newBuilder().setId(id).build();
        GetAOrderResponse orderResponse = stub.getAOrder(orderRequest);
        return OrderResponse.builder()
                .id(orderResponse.getId())
                .amount(orderResponse.getAmount())
                .orderStatus(OrderStatusEnum.valueOf(orderResponse.getOrderStatus().name()))
                .build();
    }

    public OrderResponse createOrder(OrderRequest request) {
        OrderServiceGrpc.OrderServiceBlockingStub stub = grpcClientService.getOrderServiceStub();

        CreateAOrderRequest orderRequest =
                CreateAOrderRequest.newBuilder()
                        .setAmount(request.getAmount())
                        .setProductId(request.getProductId())
                        .build();

        GetAOrderResponse orderResponse = stub.createAOrder(orderRequest);
        return OrderResponse.builder()
                .id(orderResponse.getId())
                .amount(orderResponse.getAmount())
                .orderStatus(OrderStatusEnum.valueOf(orderResponse.getOrderStatus().name()))
                .build();
    }
}

