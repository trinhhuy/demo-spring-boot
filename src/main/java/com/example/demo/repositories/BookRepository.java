package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Book;
import com.example.demo.models.User;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByUser(User user, Pageable pageable);

    Optional<Book> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);
}
