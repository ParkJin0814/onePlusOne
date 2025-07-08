package com.example.oneplusone.domain.product.service;

import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse productSearch(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
        return new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity());
    }

    public Page<ProductResponse> productsPage(String search, Pageable pageable) {
        Page<Product> Products = productRepository.findByProduct(search, pageable);
        return Products.map(product -> new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity()));
    }
}