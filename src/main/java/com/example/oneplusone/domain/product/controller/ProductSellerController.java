package com.example.oneplusone.domain.product.controller;

import com.example.oneplusone.domain.common.dto.ApiResponse;
import com.example.oneplusone.domain.common.security.UserDetailsImpl;
import com.example.oneplusone.domain.product.dto.request.CreateProductRequest;
import com.example.oneplusone.domain.product.dto.request.UpdateProductRequest;
import com.example.oneplusone.domain.product.dto.response.ProductSellerResponse;
import com.example.oneplusone.domain.product.service.ProductSellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductSellerController {

    private final ProductSellerService productService;

    @PostMapping("/seller/products")
    public ResponseEntity<ApiResponse<ProductSellerResponse>> createProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CreateProductRequest request
    ) {
        ProductSellerResponse product = productService.createProduct(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("상품이 생성되었습니다.", product));
    }

    @DeleteMapping("/seller/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        productService.deleteProduct(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("상품이 삭제되었습니다.", null));
    }

    @PatchMapping("/seller/products/{id}")
    public ResponseEntity<ApiResponse<ProductSellerResponse>> updateProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductSellerResponse product = productService.updateProduct(id, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("상품이 수정되었습니다.", product));
    }

}