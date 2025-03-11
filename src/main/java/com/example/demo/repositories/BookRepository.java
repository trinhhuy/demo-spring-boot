package com.example.demo.repositories;

import com.example.demo.models.Book;
import com.example.demo.models.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByUser(User user, Pageable pageable);
    Optional<Book> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
}