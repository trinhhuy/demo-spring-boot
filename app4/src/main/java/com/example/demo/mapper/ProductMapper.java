package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.GetAProductResponse;
import com.example.demo.models.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    GetAProductResponse toGetAProductResponse(Product product);
}
