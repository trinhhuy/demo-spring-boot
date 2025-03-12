package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Error response model")
@Data
public class ErrorResponse {
    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Error message")
    private String message;
}

