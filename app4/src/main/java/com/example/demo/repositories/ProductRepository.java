package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE id > :lastId ORDER BY id LIMIT :size", nativeQuery = true)
    List<Product> findNextPage(@Param("lastId") Long lastId, @Param("size") int size);
}
