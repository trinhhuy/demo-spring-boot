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

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private App1Service app1Service;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        // return "xxxx";
        return "Response from app1: " + app1Service.getHelloFromApp1();
        // return userService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return jwtUtil.generateToken(request.getUsername());
    }
}
