package com.example.demo.controllers;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.App1Service;
import com.example.demo.services.UserService;
import com.example.demo.services.LoggingService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private App1Service app1Service;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private LoggingService loggingService;
    
    // Add MeterRegistry for metrics
    private final MeterRegistry meterRegistry;
    private final Counter loginAttemptsCounter;
    private final Counter registerAttemptsCounter;
    private final Timer tokenGenerationTimer;
    
    @Autowired
    public AuthController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Login metrics
        this.loginAttemptsCounter = Counter.builder("auth_login_attempts_total")
            .description("Total number of login attempts")
            .register(meterRegistry);
            
        // Registration metrics
        this.registerAttemptsCounter = Counter.builder("auth_registration_attempts_total")
            .description("Total number of registration attempts")
            .register(meterRegistry);
            
        // Token generation timing
        this.tokenGenerationTimer = Timer.builder("auth_token_generation_seconds")
            .description("Time taken to generate authentication tokens")
            .register(meterRegistry);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        // Increment registration counter
        registerAttemptsCounter.increment();
        
        // Tag-based counter for more detailed metrics
        meterRegistry.counter("auth_operations", 
            "operation", "register",
            "username", request.getUsername())
            .increment();
        
        String result = userService.register(request);
        return result;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        // Increment login counter
        loginAttemptsCounter.increment();
        
        loggingService.logInfo("Bắt đầu xử lý đăng nhập cho user: " + request.getUsername());
        
        // Measure token generation time
        return tokenGenerationTimer.record(() -> {
            String token = jwtUtil.generateToken(request.getUsername());
            
            // Record login outcome
            meterRegistry.counter("auth_login_outcomes", 
                "outcome", "success",
                "username", request.getUsername())
                .increment();
                
            loggingService.logInfo("Đã tạo token cho user: " + request.getUsername());
            return token;
        });
    }
}