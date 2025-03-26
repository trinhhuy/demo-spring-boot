package com.example.demo.controllers;

import com.example.demo.dto.request.OrderRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.services.App5ServiceImpl;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/test/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestApp5Controller {
    App5ServiceImpl orderService;

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable("id") long id) {
        return orderService.getOrder(id);
    }

    @PostMapping()
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
}
