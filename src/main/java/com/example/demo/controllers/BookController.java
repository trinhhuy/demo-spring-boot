package com.example.demo.controllers;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.models.Book;
import com.example.demo.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<Page<BookResponse>> getAllBooks(@RequestHeader("Authorization") String token, Pageable pageable) {
        Page<BookResponse> books = bookService.getUserBooks(token.replace("Bearer ", ""), pageable);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<BookResponse> addBook(@RequestHeader("Authorization") String token, @RequestBody BookRequest bookRequest) {
        BookResponse book = bookService.addBook(token.replace("Bearer ", ""), bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        BookResponse book = bookService.getBookById(token.replace("Bearer ", ""), id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@RequestHeader("Authorization") String token, @RequestBody BookRequest bookRequest, @PathVariable Long id) {
        BookResponse book = bookService.updateBook(token.replace("Bearer ", ""), bookRequest, id);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        bookService.deleteBook(token.replace("Bearer ", ""), id);
        return ResponseEntity.noContent().build();
    }
}
