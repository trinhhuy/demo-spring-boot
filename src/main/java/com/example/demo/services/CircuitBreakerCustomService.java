package com.example.demo.services;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ServiceFailureException;
import com.example.demo.dto.CircuitBreakerTestRequest;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.CompletableFuture;

@Service
public class CircuitBreakerCustomService {
    private static final String BACKEND = "example";
    private static final String BANK_A_BACKEND = "bank-a";
    private static final String BANK_B_BACKEND = "bank-b";

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final MeterRegistry meterRegistry;

    @Autowired
    public CircuitBreakerCustomService(CircuitBreakerRegistry circuitBreakerRegistry, 
                                     MeterRegistry meterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.meterRegistry = meterRegistry;
    }

    public CompletableFuture<String> testWithCustomConfig(CircuitBreakerTestRequest request) {
        String bankName = request.getBankName().toUpperCase();
        
        // Lấy circuit breaker instance tương ứng với từng bank
        CircuitBreaker circuitBreaker;
        switch(bankName) {
            case "BANK_A":
                circuitBreaker = circuitBreakerRegistry.circuitBreaker(BANK_A_BACKEND);
                return handleBankA(circuitBreaker, request.getOperation());
            case "BANK_B":
                circuitBreaker = circuitBreakerRegistry.circuitBreaker(BANK_B_BACKEND);
                return handleBankB(circuitBreaker, request.getOperation());
            default:
                throw new IllegalArgumentException("Invalid bank name: " + bankName);
        }
    }

    private void recordMetrics(String bankName, String operation, boolean isSuccess) {
        meterRegistry.counter("circuit_breaker_requests_total",
            "bank", bankName,
            "operation", operation,
            "status", isSuccess ? "success" : "failure"
        ).increment();
    }

    private CompletableFuture<String> handleBankA(CircuitBreaker circuitBreaker, String operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
                    // Bank A xử lý chậm - 2 giây
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted", e);
                    }
                    
                    // Nếu operation là ERROR_OPERATION, tăng tỷ lệ lỗi lên 80%
                    if ("ERROR_OPERATION".equals(operation)) {
                        if (Math.random() < 0.8) { // 80% tỷ lệ lỗi
                            throw new ServiceFailureException("Operation failed at BANK_A - High failure rate for ERROR_OPERATION");
                        }
                    } else {
                        // Operation bình thường - giữ tỷ lệ lỗi 40%
                        if (Math.random() < 0.4) {
                            throw new ServiceFailureException("Operation failed at BANK_A - Normal failure rate");
                        }
                    }
                    
                    recordMetrics("BANK_A", operation, true);
                    return String.format("Operation '%s' successful at BANK_A", operation);
                }).get();
            } catch (Exception e) {
                recordMetrics("BANK_A", operation, false);
                if (e.getCause() instanceof ServiceFailureException) {
                    return "Fallback: Chuyển sang xử lý offline cho BANK_A - " + operation;
                }
                return "Fallback: Hệ thống BANK_A tạm thời không khả dụng, vui lòng thử lại sau";
            }
        });
    }

    private CompletableFuture<String> handleBankB(CircuitBreaker circuitBreaker, String operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
                    // Bank B xử lý nhanh - 1 giây
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted", e);
                    }
                    
                    // Nếu operation là NORMAL_OPERATION, giảm tỷ lệ lỗi xuống 5%
                    if ("NORMAL_OPERATION".equals(operation)) {
                        if (Math.random() < 0.05) { // 5% tỷ lệ lỗi
                            throw new ServiceFailureException("Operation failed at BANK_B - Low failure rate for NORMAL_OPERATION");
                        }
                    } else {
                        // Operation khác - giữ tỷ lệ lỗi 20%
                        if (Math.random() < 0.2) {
                            throw new ServiceFailureException("Operation failed at BANK_B - Normal failure rate");
                        }
                    }
                    
                    recordMetrics("BANK_B", operation, true);
                    return String.format("Operation '%s' successful at BANK_B", operation);
                }).get();
            } catch (Exception e) {
                recordMetrics("BANK_B", operation, false);
                if (e.getCause() instanceof ServiceFailureException) {
                    return "Fallback: Chuyển sang hệ thống dự phòng cho BANK_B - " + operation;
                }
                return "Fallback: Hệ thống BANK_B đang bảo trì, vui lòng thử lại sau";
            }
        });
    }

    // Thêm method mới để lấy trạng thái của circuit breaker
    public String getCircuitBreakerStatus(String bankName) {
        String backend;
        switch(bankName.toUpperCase()) {
            case "BANK_A":
                backend = BANK_A_BACKEND;
                break;
            case "BANK_B":
                backend = BANK_B_BACKEND;
                break;
            default:
                throw new IllegalArgumentException("Invalid bank name: " + bankName);
        }
        
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(backend);
        return String.format("Bank: %s, State: %s, Failure Rate: %.2f%%", 
            bankName,
            circuitBreaker.getState(),
            circuitBreaker.getMetrics().getFailureRate());
    }
}