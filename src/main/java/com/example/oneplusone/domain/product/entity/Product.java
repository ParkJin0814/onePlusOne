package com.example.oneplusone.domain.product.entity;

import com.example.oneplusone.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long quantity;

    public Product(String name, String type, Long price, Long quantity, User user) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.user = user;
    }

    public void updateProduct(String name, String type, Long price, Long quantity) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

}