package com.example.oneplusone.domain.orders.repository;

import com.example.oneplusone.domain.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByProductId(Long productId, Pageable pageable);
}
