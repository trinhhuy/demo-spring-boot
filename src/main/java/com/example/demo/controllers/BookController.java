package com.example.demo.controllers;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.response.AppResponse;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.response.ResponseUtils;
import com.example.demo.services.BookService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<AppResponse<BookResponse>> addBook(@RequestBody BookRequest bookRequest) {
        BookResponse book = bookService.addBook(bookRequest);
        return ResponseUtils.created(book);
    }

    @Operation(summary = "Get the Book", description = "Get the Book API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user data"
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

    })
    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<BookResponse>> getBookById(
            @Parameter(description = "ID of the book to be fetched") @PathVariable Long id
    ) {
        BookResponse book = bookService.getBookById(id);
        return ResponseUtils.success(book);
    }

    @Operation(summary = "Update A Book", description = "Update A Book API")
    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<BookResponse>> updateBook(
            @RequestBody BookRequest bookRequest,
            @Parameter(description = "ID of the book to be updated") @PathVariable Long id
    ) {
        BookResponse book = bookService.updateBook(bookRequest, id);
        return ResponseUtils.success(book);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @Operation(summary = "Remove A Book", description = "Remove A Book API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to be removed") @PathVariable Long id
    ) {
        bookService.deleteBook(id);
        return ResponseUtils.noContent();
    }
}
