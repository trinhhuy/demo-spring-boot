package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(5000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    BAD_REQUEST(4000, "Bad request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(4001, "You do not have permission", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTED(4002, "Username existed", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_EXISTED(4003, "Username not existed", HttpStatus.NOT_FOUND),
    PASSWORD_NOT_CORRECT(4004, "Password not correct", HttpStatus.BAD_REQUEST),
    INVALID_DTO(4005, "Invalid DTO", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    int code;
    String message;
    HttpStatusCode statusCode;
}
