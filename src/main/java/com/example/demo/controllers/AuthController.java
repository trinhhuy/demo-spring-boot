package com.example.demo.controllers;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AppResponse;
import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.ResponseUtils;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.App1Service;
import com.example.demo.services.LoggingService;
import com.example.demo.services.UserService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Controller Auth Management")
public class AuthController {

    private final UserService userService;

    @Autowired
    private App1Service app1Service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoggingService loggingService;

    private final MeterRegistry meterRegistry;

    // COUNTER - Increases monotonically
    private final Counter loginAttemptsCounter;
    private final Counter failedLoginCounter;

    // GAUGE - Can go up and down, tracks current value
    private final AtomicInteger activeLoginSessions = new AtomicInteger(0);

    // HISTOGRAM (implemented via Timer) - Measures distribution of values
    private final Timer loginResponseTimeTimer;

    @Autowired
    public AuthController(UserService userService, MeterRegistry meterRegistry) {
        this.userService = userService;
        this.meterRegistry = meterRegistry;

        // COUNTER examples
        this.loginAttemptsCounter = Counter.builder("auth.login.attempts.total")
                .description("Total number of login attempts")
                .register(meterRegistry);

        this.failedLoginCounter = Counter.builder("auth.login.failed.total")
                .description("Total number of failed login attempts")
                .register(meterRegistry);

        // GAUGE example - tracks current active sessions
        Gauge.builder("auth.sessions.active", activeLoginSessions, AtomicInteger::get)
                .description("Number of currently active login sessions")
                .register(meterRegistry);

        // HISTOGRAM example (via Timer with histogram statistics enabled)
        this.loginResponseTimeTimer = Timer.builder("auth.login.duration")
                .description("Distribution of login request processing times")
                .publishPercentiles(0.5, 0.95, 0.99) // Publish 50th, 95th, 99th percentiles
                .publishPercentileHistogram() // Enable histogram
                .sla(
                        Duration.ofMillis(10), // 10ms SLO bucket
                        Duration.ofMillis(50), // 50ms SLO bucket
                        Duration.ofMillis(100) // 100ms SLO bucket
                        )
                .register(meterRegistry);
    }

    @Operation(summary = "Register user", description = "Add User API")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "User registered successfully!"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Resource already existed",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/register")
    public ResponseEntity<AppResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        // COUNTER - Increment for each registration attempt
        meterRegistry.counter("auth.register.attempts.total").increment();

        String result = userService.register(request);

        // COUNTER with tags - for successful registrations
        meterRegistry
                .counter("auth.register.outcomes", "success", result.contains("success") ? "true" : "false")
                .increment();

        return ResponseUtils.created(Map.of("message", "User registered successfully!"));
    }

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "User login successfully!"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Resource already existed",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/login")
    public ResponseEntity<AppResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        // COUNTER - Track total login attempts
        loginAttemptsCounter.increment();

        loggingService.logInfo("Bắt đầu xử lý đăng nhập cho user: " + request.getUsername());

        // HISTOGRAM (via Timer) - Measure login response time
        return loginResponseTimeTimer.record(() -> {
            try {
                LoginResponse res = userService.login(request);
                return ResponseUtils.success(res);
            } catch (Exception e) {
                // COUNTER - Track failed logins
                failedLoginCounter.increment();
                throw e;
            }
        });
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        // GAUGE - Decrement active sessions on logout
        activeLoginSessions.decrementAndGet();

        return "Logged out successfully";
    }
}
