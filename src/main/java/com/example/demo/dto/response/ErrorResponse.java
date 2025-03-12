package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Error response model")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {
    @Schema(description = "HTTP status code")
    private int code;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Error descriptions")
    private T errors;
}

