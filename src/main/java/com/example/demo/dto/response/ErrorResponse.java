package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Error response model")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ErrorResponse<T> {
    @Schema(description = "Response Status", example = "error")
    @Builder.Default
    private String status = "error";

    @Schema(description = "Error details")
    private ErrorDetail<T> error;
}
