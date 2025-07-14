package com.example.oneplusone.domain.product.service;

import com.example.oneplusone.domain.common.cachekey.CacheKeyConstants;
import com.example.oneplusone.domain.common.dto.PagedResponse;
import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Cacheable(value = CacheKeyConstants.PRODUCT)
    public ProductResponse productSearch(Long id) {

        Product product = productRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
        return new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity());
    }

    public Page<ProductResponse> productsPage(String search, Pageable pageable) {
        Page<Product> Products = productRepository.findByProduct(search, pageable);
        return Products.map(product -> new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity()));
    }

    @Cacheable(value = CacheKeyConstants.SEARCH_PRODUCT)
    public PagedResponse<ProductResponse> productsPageV2(String search, Pageable pageable) {
        // 캐시가 없는경우에 아래 내용 실행
        Page<Product> products = productRepository.findByProduct(search, pageable);

        return PagedResponse.from(products.map(product -> new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity())));
    }
}