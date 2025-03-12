package com.example.demo.services;

import com.example.demo.dto.request.BookRequest;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.BookMapper;
import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.repositories.BookRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {

    private static final int MAX_PAGE_SIZE = 5; // Giới hạn kích thước trang

    UserService userService;

    private BookMapper mapper;

    private BookRepository bookRepository;

    private UserRepository userRepository;

    private JwtUtil jwtUtil;

    public Page<BookResponse> getUserBooks(Pageable pageable) {
        // Giới hạn kích thước trang
        if (pageable != null && pageable.getPageSize() > MAX_PAGE_SIZE) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    MAX_PAGE_SIZE,
                    pageable.getSort()
            );
        }

        User user = userService.getCurrentUser();
        Page<Book> books = bookRepository.findByUser(user, pageable);

        return books.map(book -> new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor()
        ));
    }

    public BookResponse addBook(BookRequest bookRequest) {
        User user = userService.getCurrentUser();

        Book book = mapper.toBook(bookRequest);
        book.setUser(user);

        bookRepository.save(book);

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor()
        );
    }

    public BookResponse getBookById(Long id) {
        User user = userService.getCurrentUser();

        Book book = bookRepository.findByIdAndUser(id, user).orElse(null);

        if (book == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return mapper.toBookResponse(book);
    }

    public BookResponse updateBook(BookRequest bookRequest, Long id) {
        User user = userService.getCurrentUser();

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

    public void deleteBook(Long id) {
        User user = userService.getCurrentUser();

        Book book = bookRepository.findByIdAndUser(id, user).orElseThrow();

        bookRepository.delete(book);

    }
}
