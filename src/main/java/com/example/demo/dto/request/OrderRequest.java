package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
public class OrderRequest {

    @PositiveOrZero()
    @NotNull
    private Double amount;

    @Min(value = 1)
    @NotNull
    private Long productId;

}