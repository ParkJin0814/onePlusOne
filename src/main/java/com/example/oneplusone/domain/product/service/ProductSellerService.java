package com.example.oneplusone.domain.product.service;

import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.enums.UserRole;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.product.dto.request.CreateProductRequest;
import com.example.oneplusone.domain.product.dto.request.UpdateProductRequest;
import com.example.oneplusone.domain.product.dto.response.ProductSellerResponse;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSellerService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public ProductSellerResponse createProduct(CreateProductRequest request) {
        // TODO : 임시로 user 생성함 -> 토큰에서 로그인 유저 정보 가져올 예정
        User user = new User("nickname", "login", "password", UserRole.SELLER);
        userRepository.save(user);

        Product product = new Product(
                request.getProductName(),
                request.getType(),
                request.getPrice(),
                request.getQuantity(),
                user
        );

        productRepository.save(product);

        return ProductSellerResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        // TODO : 임시로 USER를 찾음 -> 토큰에서 로그인 유저 정보 가져올 예정
        User user = userRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Product product = getProductById(id);

        // 본인이 생성한 Product 만 삭제가 가능하다.
        if (!product.getUser().equals(user)) throw new BaseException(ErrorCode.PRODUCT_IS_NOT_YOURS);

        productRepository.delete(product);
    }

    @Transactional
    public ProductSellerResponse updateProduct(Long id, UpdateProductRequest request) {
        // TODO : 임시로 USER를 찾음 -> 토큰에서 로그인 유저 정보 가져올 예정
        User user = userRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Product product = getProductById(id);

        // 본인이 생성한 Product 만 수정이 가능하다.
        if (!product.getUser().equals(user)) throw new BaseException(ErrorCode.PRODUCT_IS_NOT_YOURS);

        product.updateProduct(
                request.getProductName(),
                request.getType(),
                request.getPrice(),
                request.getQuantity()
        );

        return ProductSellerResponse.from(product);
    }
}