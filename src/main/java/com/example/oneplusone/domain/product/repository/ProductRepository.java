package com.example.oneplusone.domain.product.repository;

import com.example.oneplusone.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
        SELECT p FROM Product p
        WHERE (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%'))
    """)
    Page<Product> findByProduct(String search, Pageable pageable);
}
