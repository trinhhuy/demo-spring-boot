package com.example.demo.services;

import java.util.Optional;

import com.example.demo.*;
import com.example.demo.dto.message.OrderCreatedMessage;
import com.example.demo.enums.OrderStatusEnum;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.models.Order;
import com.example.demo.repositories.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderGrpcServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    OrderRepository repository;
    OrderMapper orderMapper;
    KafkaProducerServiceImpl kafkaProducer;

    @Override
    public void getAOrder(GetAOrderRequest request, StreamObserver<GetAOrderResponse> responseObserver) {
        long id = request.getId();
        Optional<Order> order = repository.findById(id);
        if (order.isEmpty()) {
            throw Status.NOT_FOUND
                    .withDescription("Book not found with ID: " + request.getId())
                    .asRuntimeException();
        }

        responseObserver.onNext(orderMapper.toGetAOrderResponse(order.get()));
        responseObserver.onCompleted();
    }

    @Override
    public void createAOrder(CreateAOrderRequest request, StreamObserver<GetAOrderResponse> responseObserver) {
        Order order = orderMapper.toOrder(request);
        order.setStatus(OrderStatusEnum.ORDER_CREATED);
        log.info("Order created: {}", order.toString());

        repository.save(order);

        String msg = toMsg(orderMapper.toOrderCreatedMessage(order));

        kafkaProducer.sendMessage("order-created", order.getId().toString(), msg);
        log.info("Order Created Event sent for Order ID: {}", order.getId());
        log.info(order.toString());

        GetAOrderResponse x = orderMapper.toGetAOrderResponse(order);
        System.out.println(x.getOrderStatus());
        responseObserver.onNext(orderMapper.toGetAOrderResponse(order));
        responseObserver.onCompleted();
    }

    private String toMsg(OrderCreatedMessage order) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
