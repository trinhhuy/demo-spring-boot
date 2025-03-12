package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Response DTO", description = "Book Response DTO")
public class BookResponse {

    @Schema(description = "ID", example = "1")
    private long id;

    @Schema(description = "Title", example = "7 Habits")
    private String title;

    @Schema(description = "Author", example = "Salmon Odin")
    private String author;
}
