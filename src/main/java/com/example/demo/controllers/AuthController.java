package com.example.demo.controllers;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AppResponse;
import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.ResponseUtils;
import com.example.demo.models.User;
import com.example.demo.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Auth Controller", description = "Controller Auth Management")
public class AuthController {

    UserService userService;

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
        userService.register(request);
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
        LoginResponse res = userService.login(request);
        return ResponseUtils.success(res);
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
