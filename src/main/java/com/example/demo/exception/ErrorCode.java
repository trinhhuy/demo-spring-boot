package com.example.demo.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(5000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SOCKET_TIMEOUT_EXCEPTION(5001, "Socket Timeout error", HttpStatus.GATEWAY_TIMEOUT),
    RESOURCE_ACCESS_EXCEPTION(5002, "Resource Access error", HttpStatus.GATEWAY_TIMEOUT),

    BAD_REQUEST(4000, "Bad request", HttpStatus.BAD_REQUEST),
    INVALID_DTO(4001, "Invalid DTO", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(4002, "Username existed", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_CORRECT(4003, "Password not correct", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_EXISTED(4004, "Username not existed", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(4005, "You do not have permission", HttpStatus.UNAUTHORIZED),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;
}
