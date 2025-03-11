package com.example.demo.dto;

import lombok.Data;

@Data
public class BookRequest {

//    @NotBlank(message = "Title is required")
    private String title;
// @NotBlank(message = "Author is required")
    private String author;
}
