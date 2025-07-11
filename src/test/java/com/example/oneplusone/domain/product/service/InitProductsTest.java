package com.example.oneplusone.domain.product.service;

import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.enums.UserRole;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class InitProductsTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    @Test
    @DisplayName("5만개의 더미 데이터 생성")
    void createProducts() {
        User user = new User("admin", "admin", "password", UserRole.SELLER);
        userRepository.save(user);
        List<Product> productList = new ArrayList<>();
        for(int i = 1; i <= 50000; i ++){
            String name = "product" + i;
            Long price = 10000L + i;
            Long quantity = 1L + i;
            Product product = new Product(name, "type", price, quantity, user);
            productList.add(product);
        }
        productRepository.saveAll(productList);
    }
}
