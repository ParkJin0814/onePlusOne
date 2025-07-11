package com.example.oneplusone.domain.orders.service;

import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.enums.UserRole;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.orders.dto.request.OrderRequest;
import com.example.oneplusone.domain.orders.dto.response.OrderResponse;
import com.example.oneplusone.domain.orders.repository.OrderRepository;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @WithMockUser(username = "testUser", roles = {"SELLER"})
    @DisplayName("동시성 제어 테스트")
    void concurrencyControl() {
        //given
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User("testUser", "testUser", "password", UserRole.SELLER);
        User saveUser = userRepository.save(testUser);
        Product testProduct = new Product("test", "test", 100L, 5L, saveUser);
        Product saveProduct = productRepository.save(testProduct);

        ExecutorService executor = Executors.newFixedThreadPool(5);
        OrderRequest orderRequest = new OrderRequest(1L);


        //when
        List<Future<OrderResponse>> results = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            // db 비관적 락 기능
            Future<OrderResponse> submit = executor.submit(() -> orderService.orderProductDbLock(orderRequest, saveProduct.getId(), saveUser.getId()));

            results.add(submit);
        }
        for (Future<OrderResponse> result : results) {
            try {
                result.get();  // 작업이 끝날 때까지 블록됨, 예외 발생 시 던짐
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(); // 꼭 출력!
                fail("예외 발생: " + e.getCause()); // e.getCause()가 실제 예외
            }
        }

        Product product = productRepository.findById(saveProduct.getId()).orElseThrow();

        //then
        assertThat(product.getQuantity()).isEqualTo(0L);
    }
}