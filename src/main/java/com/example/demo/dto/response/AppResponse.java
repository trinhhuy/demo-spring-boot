package com.example.demo.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic response wrapper")
public class AppResponse<T> {
    @Schema(description = "Response Status", example = "success")
    @Builder.Default
    private String status = "success";

    @Schema(description = "Response Data")
    private T data;

    @Schema(description = "Response Metadata")
    private Map<String, Object> metadata;
}
