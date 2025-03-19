package com.example.demo.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {
    
    private static final String BACKEND = "example";
    
    @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
    public String doSomething() {
        // Giả lập một service call có thể bị lỗi
        if(Math.random() < 0.7) { // 70% xác suất gây lỗi
            throw new RuntimeException("Service failed!");
        }
        return "Success";
    }
    
    public String fallback(Exception e) {
        return "Fallback response do service bị lỗi: " + e.getMessage();
    }
} 