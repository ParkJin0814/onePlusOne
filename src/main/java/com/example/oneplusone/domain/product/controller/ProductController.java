package com.example.oneplusone.domain.product.controller;

import com.example.oneplusone.domain.common.dto.ApiResponse;
import com.example.oneplusone.domain.common.dto.PagedResponse;
import com.example.oneplusone.domain.common.security.UserDetailsImpl;
import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import com.example.oneplusone.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> productSearch(@PathVariable Long id) {
        ProductResponse product = productService.productSearch(id);
        return ResponseEntity.ok(ApiResponse.ok("상품이 조회되었습니다.", product));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> productsPage(@RequestParam(required = false) String search,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.productsPage(search, pageable);
        return ResponseEntity.ok(ApiResponse.ok("상품이 조회되었습니다.", PagedResponse.from(products)));
    }

    @GetMapping("/products/v2")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> productsPageV2(@RequestParam(required = false) String search,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.productsPageV2(search, pageable, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("상품이 조회되었습니다.", PagedResponse.from(products)));
    }

}
