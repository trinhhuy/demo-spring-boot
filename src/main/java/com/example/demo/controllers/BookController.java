package com.example.demo.controllers;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.services.BookService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Book Controller", description = "Controller Book Management")
public class BookController {
    BookService bookService;

    @Operation(summary = "Get Book List", description = "Get Book List API")
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks( @RequestParam(required = false) Pageable pageable) {
        Page<BookResponse> books = bookService.getUserBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Add A Book", description = "Add A Book API")
    @PostMapping
    public ResponseEntity<BookResponse> addBook(@RequestBody BookRequest bookRequest) {
        BookResponse book = bookService.addBook(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @Operation(summary = "Get the Book", description = "Get the Book API")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "ID of the book to be fetched") @PathVariable Long id
    ) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Update A Book", description = "Update A Book API")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @RequestBody BookRequest bookRequest,
            @Parameter(description = "ID of the book to be updated") @PathVariable Long id
    ) {
        BookResponse book = bookService.updateBook(bookRequest, id);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Remove A Book", description = "Remove A Book API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to be removed") @PathVariable Long id
    ) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
