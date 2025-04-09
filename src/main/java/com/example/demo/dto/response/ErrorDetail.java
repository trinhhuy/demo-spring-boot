package com.example.demo.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Error detail")
public class ErrorDetail<T> {
    @Schema(description = "Http Status Code", example = "500")
    private int code;

    @Schema(description = "message", example = "Internal Server Error")
    private String message;

    @Schema(
            description = "Error info",
            example =
                    "{" + "\"service\": \"Payment\"," + "\"issue\": \"Timeout while connecting to the gateway\"" + "}")
    private List<T> details;
}
