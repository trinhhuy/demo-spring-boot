package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class CircuitBreakerException extends ServiceFailureException {
    public CircuitBreakerException(String message) {
        super(message);
    }
} 