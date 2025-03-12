package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "Book Request DTO", description = "Book Request DTO")
public class BookRequest {

    @Schema(description = "Title", example = "7 Habits")
    @NotBlank(message = "Title is required")
    private String title;


    @Schema(description = "Author", example = "Salmon Odin")
    @NotBlank(message = "Author is required")
    private String author;
}
