package com.example.oneplusone.domain.product.service;

import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import com.example.oneplusone.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final SearchService searchService;

    public ProductResponse productSearch(Long id) {
        ProductResponse cacheResponse = searchService.cacheProductSearch(id);
        if(cacheResponse != null) {
            return cacheResponse;
        }
        Product product = productRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
        ProductResponse productResponse = new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity());
        searchService.createProductCache(id, productResponse);
        return productResponse;
    }

    public Page<ProductResponse> productsPage(String search, Pageable pageable) {
        Page<Product> Products = productRepository.findByProduct(search, pageable);
        return Products.map(product -> new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity()));
    }

    public Page<ProductResponse> productsPageV2(String search, Pageable pageable, Long userId) {

        // 캐시를 확인
        Page<ProductResponse> cacheResponse = searchService.cachePageSearch(search, pageable, userId);
        // 캐시가 존재하면 캐시 값을 반환
        if(cacheResponse != null) {
            return cacheResponse;
        }

        // 캐시가 존재하지 않는 경우 DB에 접근
        Page<Product> products = productRepository.findByProduct(search, pageable);
        Page<ProductResponse> productResponses = products.map(product -> new ProductResponse(product.getId(), product.getName(), product.getType(), product.getPrice(), product.getQuantity()));
        // 인기 검색어를 위한 검색어 카운트
        searchService.createPageCache(productResponses, search, pageable, userId);

        return productResponses;
    }
}