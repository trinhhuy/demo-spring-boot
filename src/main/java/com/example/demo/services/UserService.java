package com.example.demo.services;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    private final Counter registrationCounter;
    private final Counter registrationSuccessCounter;
    private final Counter registrationFailureCounter;
    private final Timer registrationTimer;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, MeterRegistry registry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;

        // Initialize counters and timers
        this.registrationCounter = Counter.builder("app.users.registration.attempts")
            .description("Total number of user registration attempts")
            .register(registry);
            
        this.registrationSuccessCounter = Counter.builder("app.users.registration.success")
            .description("Number of successful user registrations")
            .register(registry);
            
        this.registrationFailureCounter = Counter.builder("app.users.registration.failures")
            .description("Number of failed user registrations")
            .register(registry);
            
        this.registrationTimer = Timer.builder("app.users.registration.duration")
            .description("Time taken to register a new user")
            .register(registry);
    }


    public String register(RegisterRequest request) {
        // Increment the registration attempt counter
        registrationCounter.increment();
        
        // Use timer to measure registration duration
        return registrationTimer.record(() -> {
            try {
                boolean checkUsernameExists = userRepository.existsByUsername(request.getUsername());
                if (checkUsernameExists) {
                    throw (new AppException(ErrorCode.RESOURCE_EXISTED))
                            .withDetails(Map.of("username", request.getUsername(), "issue", "username already existed"));
                }

                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
                return "User registered successfully!";
            } catch (Exception e) {
                // Increment failure counter
                registrationFailureCounter.increment();
                throw e;
            }
        });
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            throw (new AppException(ErrorCode.RESOURCE_NOT_EXISTED))
                    .withDetails(Map.of("username", request.getUsername(), "issue", "username not found"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw (new AppException(ErrorCode.UNAUTHORIZED)).withDetails(Map.of("issue", "Wrong password"));
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            return userRepository.findByUsername(username).orElseThrow();
        }
        return null;
    }

    public UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails;
        }
        return null;
    }
}
