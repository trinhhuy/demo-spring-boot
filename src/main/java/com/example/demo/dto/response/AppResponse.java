package com.example.demo.dto.response;

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
    @Schema(description = "Response code", example = "200")
    @Builder.Default
    private int code = 200;

    @Schema(description = "Response message", example = "Success")
    private String message;

    @Schema(description = "Response result")
    private T result;
}
