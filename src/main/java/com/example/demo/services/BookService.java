package com.example.demo.services;

import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public List<Book> getUserBooks(String token) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();
        return bookRepository.findByUser(user);
    }

    public Book addBook(String token, Book book) {
        String username = jwtUtil.validateToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();
        book.setUser(user);
        return bookRepository.save(book);
    }
}
