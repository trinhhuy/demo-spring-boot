package com.example.demo.exception;

public class ServiceFailureException extends RuntimeException {
    public ServiceFailureException(String message) {
        super(message);
    }
} 