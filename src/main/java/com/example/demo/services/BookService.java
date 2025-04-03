package com.example.demo.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.request.PageableDTO;
import com.example.demo.dto.response.AppResponse;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.dto.response.PaginationResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
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

    public AppResponse<List<BookResponse>> list(PageableDTO pageable) {
        User user = userService.getCurrentUser();
        Page<Book> books = bookRepository.findByUser(user, pageable.toPageable());

        List<BookResponse> data = books.stream().map(bookMapper::toBookResponse).toList();

        Map<String, Object> metadata = new HashMap<>();
        PaginationResponse paginationResponse = PaginationResponse.builder()
                .page(pageable.getPage())
                .size(pageable.getSize())
                .totalElements(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .numberOfElements(books.getNumberOfElements())
                .build();
        metadata.put("pagination", paginationResponse);

        AppResponse<List<BookResponse>> response = new AppResponse<>();
        response.setData(data);
        response.setMetadata(metadata);
        return response;
    }

    public BookResponse create(BookRequest bookRequest) {
        User user = userService.getCurrentUser();

        Book book = bookMapper.toBook(bookRequest);
        book.setUser(user);

        bookRepository.save(book);

        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor());
    }

    public BookResponse get(Long id) {
        User user = userService.getCurrentUser();

        Book book = bookRepository.findByIdAndUser(id, user).orElse(null);

        if (book == null) {
            throw (new AppException(ErrorCode.NOT_FOUND)).withDetails(Map.of("bookId", id, "userId", user.getId()));
        }
        return bookMapper.toBookResponse(book);
    }

    public BookResponse update(BookRequest bookRequest, Long id) {
        User user = userService.getCurrentUser();

        if (!bookRepository.existsByIdAndUser(id, user)) {
            throw (new AppException(ErrorCode.NOT_FOUND)).withDetails(Map.of("bookId", id, "userId", user.getId()));
        }

        Book book = Book.builder()
                .id(id)
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .user(user) // không chuyển sang cho user khác mà chỉ update thông tin sách
                .build();

        bookRepository.save(book);

        return bookMapper.toBookResponse(book);
    }

    public void delete(Long id) {
        User user = userService.getCurrentUser();

        if (!bookRepository.existsByIdAndUser(id, user)) {
            throw (new AppException(ErrorCode.NOT_FOUND)).withDetails(Map.of("bookId", id, "userId", user.getId()));
        }

        bookRepository.deleteById(id);
    }
}
