package com.example.demo.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.dto.request.book.BookCreationDto;
import com.example.demo.dto.request.book.BookFilterDto;
import com.example.demo.dto.response.book.BookResponseDto;
import com.example.demo.enums.ResponseCode;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.mapper.BookMapper;
import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.repositories.BookRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {
    UserService userService;

    private BookMapper bookMapper;

    private BookRepository bookRepository;

    public ResponseEntity<ApiResponseDto<Map<String, Object>>> list(PageableDto pageable, BookFilterDto filterDTO) {
        Specification<Book> specification = buildSpecification(filterDTO);
        Page<Book> books = bookRepository.findAll(specification, pageable.toPageable());

        List<BookResponseDto> booksResponse = books.stream().map(bookMapper::toBookResponse).toList();

        PaginationResponseDto paginationResponse = PaginationResponseDto.builder()
                .number(pageable.getPage())
                .size(pageable.getSize())
                .totalElements(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .numberOfElements(books.getNumberOfElements())
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("books", booksResponse);
        data.put("page", paginationResponse);

        return ApiResponseDto.success(data);
    }

    private Specification<Book> buildSpecification(BookFilterDto filterDTO) {
        Specification<Book> spec = Specification.where(null);

        if(filterDTO.getTitle() != null && !filterDTO.getTitle().isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + filterDTO.getTitle() + "%"));
        }
        if(filterDTO.getAuthor() != null && !filterDTO.getAuthor().isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("author"), "%" + filterDTO.getAuthor() + "%"));
        }
        if(filterDTO.getUserId() != null && !filterDTO.getUserId().isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    root.get("user").get("id").in(filterDTO.getUserId()));
        }

        return spec;
    }

    public ResponseEntity<ApiResponseDto<BookResponseDto>> create(BookCreationDto bookRequest) {
        User user = userService.getCurrentUser();

        Book book = bookMapper.toBook(bookRequest);
        book.setUser(user);

        bookRepository.save(book);

        return ApiResponseDto.success(ResponseCode.CREATED, bookMapper.toBookResponse(book));
    }

    public ResponseEntity<ApiResponseDto<BookResponseDto>> get(Long id) {
        User user = userService.getCurrentUser();

        Book book = bookRepository.findByIdAndUser(id, user).orElse(null);

        if (book == null) {
            return ApiResponseDto.error(ResponseCode.NOT_FOUND, String.format("Book with id %s not found", id));
        }
        return ApiResponseDto.success(bookMapper.toBookResponse(book));
    }

    public ResponseEntity<ApiResponseDto<BookResponseDto>> update(BookCreationDto bookRequest, Long id) {
        User user = userService.getCurrentUser();

        if (!bookRepository.existsByIdAndUser(id, user)) {
            return ApiResponseDto.error(ResponseCode.NOT_FOUND, String.format("Book with id %s not found", id));
        }

        Book book = Book.builder()
                .id(id)
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .user(user)
                .build();

        bookRepository.save(book);

        return ApiResponseDto.success(bookMapper.toBookResponse(book));
    }

    public ResponseEntity<ApiResponseDto<Void>>  delete(Long id) {
        User user = userService.getCurrentUser();

        if (!bookRepository.existsByIdAndUser(id, user)) {
            return ApiResponseDto.error(ResponseCode.NOT_FOUND, String.format("Book with id %s not found", id));
        }

        bookRepository.deleteById(id);

        return ApiResponseDto.success(ResponseCode.NO_CONTENT, null);
    }
}
