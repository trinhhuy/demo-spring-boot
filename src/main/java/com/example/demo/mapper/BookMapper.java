package com.example.demo.mapper;

import com.example.demo.dto.request.book.BookCreationDto;
import com.example.demo.dto.response.book.BookResponseDto;
import com.example.demo.models.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public Book toBook(BookCreationDto request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .build();
    }

    public BookResponseDto toBookResponse(Book book) {
        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }
}
