package com.example.oneplusone.domain.product.service;

import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.common.cachekey.CacheKeyConstants;
import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.product.dto.request.CreateProductRequest;
import com.example.oneplusone.domain.product.dto.request.UpdateProductRequest;
import com.example.oneplusone.domain.product.dto.response.ProductSellerResponse;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSellerService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = CacheKeyConstants.SEARCH_PRODUCT, allEntries = true)
    public ProductSellerResponse createProduct(CreateProductRequest request, Long userId) {
        Product product = new Product(
                request.getProductName(),
                request.getType(),
                request.getPrice(),
                request.getQuantity(),
                getUserById(userId)
        );

        productRepository.save(product);

        return ProductSellerResponse.from(product);
    }

    @Transactional
    @Caching(evict = {
            // 검색캐시 전부제거
            @CacheEvict(value = CacheKeyConstants.SEARCH_PRODUCT, allEntries = true),
            // 단일조회 캐시 키값에 해당하는것만 제거
            @CacheEvict(value = CacheKeyConstants.PRODUCT, key = "#id")
    })
    public void deleteProduct(Long id, Long userId) {
        User user = getUserById(userId);
        Product product = getProductById(id);

        // 본인이 생성한 Product 만 삭제가 가능하다.
        if (!product.getUser().equals(user)) throw new BaseException(ErrorCode.PRODUCT_IS_NOT_YOURS);

        productRepository.delete(product);
    }

    @Transactional
    @Caching(evict = {
            // 검색캐시 전부제거
            @CacheEvict(value = CacheKeyConstants.SEARCH_PRODUCT, allEntries = true),
            // 단일조회 캐시 키값에 해당하는것만 제거
            @CacheEvict(value = CacheKeyConstants.PRODUCT, key = "#id")
    })
    public ProductSellerResponse updateProduct(Long id, UpdateProductRequest request, Long userId) {
        User user = getUserById(userId);
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