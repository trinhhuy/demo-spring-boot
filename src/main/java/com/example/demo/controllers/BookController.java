package com.example.demo.controllers;

import com.example.demo.dto.BookRequest;
import com.example.demo.models.Book;
import com.example.demo.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public Page<Book> getAllBooks(@RequestHeader("Authorization") String token, Pageable pageable) {
        return bookService.getUserBooks(token.replace("Bearer ", ""), pageable);
    }

    @PostMapping
    public Book addBook(@RequestHeader("Authorization") String token, @RequestBody Book book) {
        return bookService.addBook(token.replace("Bearer ", ""), book);
    }

    @GetMapping("/{id}")
    public Book getBookById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return bookService.getBookById(token.replace("Bearer ", ""), id);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestHeader("Authorization") String token, @RequestBody BookRequest book, @PathVariable Long id) {
        return bookService.updateBook(token.replace("Bearer ", ""), book, id);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        bookService.deleteBook(token.replace("Bearer ", ""), id);
    }
}
