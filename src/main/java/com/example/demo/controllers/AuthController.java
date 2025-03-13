package com.example.demo.controllers;

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

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        String result = userService.register(request);
        return result;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        loggingService.logInfo("Bắt đầu xử lý đăng nhập cho user: " + request.getUsername());
        
        String token = jwtUtil.generateToken(request.getUsername());
        
        loggingService.logInfo("Đã tạo token cho user: " + request.getUsername());
        return token;
    }
}
