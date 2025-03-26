package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageableProductRequest {
    @Min(value = 0)
    private int page = 0;

    @Min(value = 1)
    private int size = 10;

    private String orderDirection = "desc";
}
