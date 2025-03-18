package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.models.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toBook(BookRequest request);

    BookResponse toBookResponse(Book user);
}
