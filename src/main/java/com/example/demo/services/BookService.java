package com.example.demo.services;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.BookMapper;
import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class BookService {

    private static final int MAX_PAGE_SIZE = 5; // Giới hạn kích thước trang

    @Autowired
    private BookMapper mapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Page<BookResponse> getUserBooks(String token, Pageable pageable) {
        // Giới hạn kích thước trang
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    MAX_PAGE_SIZE,
                    pageable.getSort()
            );
        }

        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();
        Page<Book> books = bookRepository.findByUser(user, pageable);

        return books.map(book -> new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor()
        ));
    }

    public BookResponse addBook(String token, BookRequest bookRequest) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        Book book = mapper.toBook(bookRequest);
        book.setUser(user);

        bookRepository.save(book);

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor()
        );
    }

    public BookResponse getBookById(String token, Long id) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        Book book = bookRepository.findByIdAndUser(id, user).orElse(null);

        if (book == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return mapper.toBookResponse(book);
    }

    public BookResponse updateBook(String token, BookRequest bookRequest, Long id) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        if (!bookRepository.existsByIdAndUser(id, user)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Book book = Book.builder()
                .id(id)
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .user(user) // không chuyển sang cho user khác mà chỉ update thông tin sách
                .build();

        bookRepository.save(book);

        return mapper.toBookResponse(book);
    }

    public void deleteBook(String token, Long id) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        Book book = bookRepository.findByIdAndUser(id, user).orElseThrow();

        bookRepository.delete(book);

    }
}
