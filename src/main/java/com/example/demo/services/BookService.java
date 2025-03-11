package com.example.demo.services;

import com.example.demo.dto.BookRequest;
import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

import java.util.List;

@Service
public class BookService {

    private static final int MAX_PAGE_SIZE = 2; // Giới hạn kích thước trang

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Page<Book> getUserBooks(String token, Pageable pageable) {
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
        return bookRepository.findByUser(user, pageable);
    }

    public Book addBook(String token, Book book) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();
        book.setUser(user);
        return bookRepository.save(book);
    }

    public Book getBookById(String token, Long id) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        return bookRepository.findByIdAndUser(id, user).orElseThrow();
    }

    public Book updateBook(String token, BookRequest bookRequest, Long id) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        Boolean exists = bookRepository.existsByIdAndUser(id, user);
        if (!exists) {
            return null; // lỗi sách không phải của bạn để update
        }

        Book book = Book.builder()
                .id(id)
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .user(user) // không chuyển sang cho user khác mà chỉ update thông tin sách
                .build();

        return bookRepository.save(book);
    }

    public void deleteBook(String token, Long id) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        Book book = bookRepository.findByIdAndUser(id, user).orElseThrow();

        bookRepository.delete(book);

    }
}
