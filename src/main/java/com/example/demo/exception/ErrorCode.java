package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Validation Errors (4xx)
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),

    // Authentication & Authorization Errors
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),

    // Resource Errors
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    RESOURCE_ACCESS_EXCEPTION(HttpStatus.GATEWAY_TIMEOUT, "Resource access error"),
    RESOURCE_EXISTED(HttpStatus.BAD_REQUEST, "Resource already existed"),
    RESOURCE_NOT_EXISTED(HttpStatus.BAD_REQUEST, "Resource not existed"),

    // Business Logic Errors
    INSUFFICIENT_FUNDS(HttpStatus.CONFLICT, "Not enough balance to complete the purchase"),

    // System Errors (5xx)
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Uncategorized exception"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"),
    SOCKET_TIMEOUT_EXCEPTION(HttpStatus.GATEWAY_TIMEOUT, "Socket timeout exception"),
    ;

    private final HttpStatusCode code;
    private final String message;

    ErrorCode(HttpStatusCode code, String message) {
        this.code = code;
        this.message = message;
    }
}
