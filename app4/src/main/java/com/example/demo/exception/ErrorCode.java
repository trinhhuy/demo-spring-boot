package com.example.demo.exception;

import io.grpc.Status;
import lombok.Getter;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND(Status.NOT_FOUND, "Product not found"),
    PRODUCT_NOT_ENOUGH(Status.NOT_FOUND, "Product not enough"),
    ORDER_NOT_FOUND(Status.NOT_FOUND, "Order not found"),
    VALIDATION_FAILED(Status.INVALID_ARGUMENT, "Invalid input"),
    DESERIALIZE_FAILED(Status.INVALID_ARGUMENT, "Deserialize failed"),
    SERIALIZE_FAILED(Status.INVALID_ARGUMENT, "Serialize failed"),
    INTERNAL_ERROR(Status.INTERNAL, "Internal server error");

    private final Status grpcStatus;
    private final String defaultMessage;

    ErrorCode(Status grpcStatus, String defaultMessage) {
        this.grpcStatus = grpcStatus;
        this.defaultMessage = defaultMessage;
    }
}
