package com.example.demo.controllers;

import java.util.Map;
import com.example.demo.dto.request.book.*;
import com.example.demo.dto.response.book.BookResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.services.BookService;

import io.swagger.v3.oas.annotations.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.example.demo.config.OpenApiConfig.*;

@RestController
@RequestMapping("/api/v1/digital-documents/library/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {
    BookService bookService;

    @Operation(summary = "Get Book List", description = "Get Book List API")
    @GetMapping
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> list(
            @ModelAttribute PageableDto pageableDTO,
            @ModelAttribute BookFilterDto filterDTO
    ) {
        return bookService.list(pageableDTO, filterDTO);
    }

    @Operation(summary = "Add Book", description = "Add Book API")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponseDto<BookResponseDto>> create(@Valid @RequestBody BookCreationDto bookRequest) {
        return bookService.create(bookRequest);
    }

    @Operation(summary = "Get the Book", description = "Get the Book API")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> get(
            @Parameter(description = "ID of the book to be fetched") @PathVariable Long id) {
        return bookService.get(id);
    }

    @Operation(summary = "Update Book", description = "Update Book API")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<BookResponseDto>> update(
            @Valid @RequestBody BookCreationDto bookRequest,
            @Parameter(description = "ID of the book to be updated") @PathVariable Long id) {
        return bookService.update(bookRequest, id);
    }

    @Operation(summary = "Remove Book", description = "Remove Book API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/" + FORBIDDEN_ERROR_RESPONSE_SCHEMA))),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponseDto<Void>> delete(@Parameter(description = "ID of the book to be removed") @PathVariable Long id) {
        return bookService.delete(id);
    }
}
