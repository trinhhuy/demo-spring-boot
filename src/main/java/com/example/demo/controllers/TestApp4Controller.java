package com.example.demo.controllers;


import java.util.List;

import com.example.demo.services.App4ServiceImpl;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.dto.request.PageableProductRequest;
import com.example.demo.dto.response.ProductResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/test/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestApp4Controller {
    App4ServiceImpl productService;

    @GetMapping("/{id}")
    public ProductResponse getAProduct(
            @PathVariable("id") int id) {
        return productService.getAProduct(id);
    }

    @GetMapping("/all")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/stream-products")
    public SseEmitter streamProducts() {
        // Gọi method từ serviceA để bắt đầu stream sản phẩm qua SSE
        return productService.streamProducts();
    }

    @GetMapping()
    public List<ProductResponse> getListProducts(
            @Valid @ModelAttribute PageableProductRequest pageableProductRequest) {
        return productService.getListProducts(pageableProductRequest);
    }
}
