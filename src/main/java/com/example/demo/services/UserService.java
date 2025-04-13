package com.example.demo.services;

import com.example.demo.dto.response.ApiResponseDto;
import com.example.demo.enums.ResponseCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.user.LoginRequestDto;
import com.example.demo.dto.request.user.RegisterRequestDto;
import com.example.demo.dto.response.user.LoginResponseDto;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final Counter registrationCounter;
    private final Counter registrationSuccessCounter;
    private final Counter registrationFailureCounter;
    private final Timer registrationTimer;

    public UserService(
            UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, MeterRegistry registry) {
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

    public ResponseEntity<ApiResponseDto<Void>> register(RegisterRequestDto request) {
        // Increment the registration attempt counter
        registrationCounter.increment();

        // Use timer to measure registration duration
        return registrationTimer.record(() -> {
            try {
                boolean checkUsernameExists = userRepository.existsByUsername(request.getUsername());
                if (checkUsernameExists) {
                    return ApiResponseDto.error(ResponseCode.BAD_REQUEST, String.format("Username %s already exists", request.getUsername()));
                }

                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
                return ApiResponseDto.success(null);
            } catch (Exception e) {
                // Increment failure counter
                registrationFailureCounter.increment();
                throw e;
            }
        });
    }

    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            return ApiResponseDto.error(ResponseCode.BAD_REQUEST, String.format("Username %s not exists", request.getUsername()));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponseDto.error(ResponseCode.UNAUTHORIZED, "Incorrect password");
        }
        String token = jwtUtil.generateToken(user.getUsername());

        return ApiResponseDto.success(new LoginResponseDto(token));
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
