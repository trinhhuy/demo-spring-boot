package com.example.demo.controllers;

import com.example.demo.models.Book;
import com.example.demo.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks(@RequestHeader("Authorization") String token) {
        return bookService.getUserBooks(token.replace("Bearer ", ""));
    }

    @PostMapping
    public Book addBook(@RequestHeader("Authorization") String token, @RequestBody Book book) {
        return bookService.addBook(token.replace("Bearer ", ""), book);
    }
}
