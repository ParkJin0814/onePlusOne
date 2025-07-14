package com.example.oneplusone.domain.order.service;

import com.example.oneplusone.domain.auth.controller.dto.LoginRequest;
import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.enums.UserRole;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.orders.dto.request.OrderRequest;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseOrderTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    // 데이터 생성
    protected User getUser(String uniqueLoginId) {
        User user = new User("nickname", uniqueLoginId, passwordEncoder.encode("password"), UserRole.SELLER);
        userRepository.save(user);
        return user;
    }

    protected Product getProduct(String uniqueLoginId) {
        User user = getUser(uniqueLoginId);

        Product product = new Product("ProductName", "type", 1000L, 100L, user);
        productRepository.save(product);
        return product;
    }

    // 토큰
    protected String getTokenByLogin(String loginId) throws Exception {
        LoginRequest request = new LoginRequest(loginId, "password");

        String signupAsString = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(signupAsString)
                .get("data")
                .get("token")
                .asText();
    }

    // 테스트할 메서드에서 상태코드 가져오기
    protected int getStatusOrderProduct(Long productId, String token, OrderRequest orderRequest, String url) throws Exception {
        return mockMvc.perform(post(url, productId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andReturn()
                .getResponse()
                .getStatus();
    }
}