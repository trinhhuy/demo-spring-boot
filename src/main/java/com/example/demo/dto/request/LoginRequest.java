package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "Login Request DTO", description = "Login Request DTO")
public class LoginRequest {
    @Schema(description = "Username", example = "my-username")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Password", example = "my-password")
    @NotBlank(message = "Password is required")
    private String password;
}
