package com.example.demo.controllers;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.models.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    
    UserService userService;
    JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return jwtUtil.generateToken(request.getUsername());
    }

    @GetMapping("/info")
    public User info() {
        return userService.getCurrentUser();
    }

    @GetMapping("/details")
    public UserDetails details() {
        return userService.getUserDetails();
    }
}
