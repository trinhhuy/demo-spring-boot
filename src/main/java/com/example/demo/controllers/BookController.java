package com.example.demo.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.request.PageableDTO;
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

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Book Controller", description = "Controller Book Management")
public class BookController {
    BookService bookService;

    @Operation(summary = "Get Book List", description = "Get Book List API")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved user data"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Resource not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping
    public ResponseEntity<AppResponse<List<BookResponse>>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort) {
        PageableDTO pageableDTO = new PageableDTO();
        pageableDTO.setPage(page);
        pageableDTO.setSize(size);
        pageableDTO.setSort(sort);

        AppResponse<List<BookResponse>> books = bookService.list(pageableDTO);
        return ResponseUtils.success(books);
    }

    @Operation(summary = "Add Book", description = "Add Book API")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Successfully add book"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Validation failed",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping
    public ResponseEntity<AppResponse<BookResponse>> create(@Valid @RequestBody BookRequest bookRequest) {
        BookResponse book = bookService.create(bookRequest);
        return ResponseUtils.created(book);
    }

    @Operation(summary = "Get the Book", description = "Get the Book API")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved user data"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Resource not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<BookResponse>> get(
            @Parameter(description = "ID of the book to be fetched") @PathVariable Long id) {
        BookResponse book = bookService.get(id);
        return ResponseUtils.success(book);
    }

    @Operation(summary = "Update Book", description = "Update Book API")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved user data"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Validation failed",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Resource not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<BookResponse>> update(
            @Valid @RequestBody BookRequest bookRequest,
            @Parameter(description = "ID of the book to be updated") @PathVariable Long id) {
        BookResponse book = bookService.update(bookRequest, id);
        return ResponseUtils.success(book);
    }

    @Operation(summary = "Remove Book", description = "Remove Book API")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "No Content"),
                @ApiResponse(responseCode = "400", description = "Bad Request"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID of the book to be removed") @PathVariable Long id) {
        bookService.delete(id);
        return ResponseUtils.noContent();
    }
}
