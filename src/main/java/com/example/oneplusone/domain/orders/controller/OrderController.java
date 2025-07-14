package com.example.oneplusone.domain.orders.controller;

import com.example.oneplusone.domain.common.dto.ApiResponse;
import com.example.oneplusone.domain.common.security.UserDetailsImpl;
import com.example.oneplusone.domain.orders.dto.request.OrderRequest;
import com.example.oneplusone.domain.orders.dto.response.OrderResponse;
import com.example.oneplusone.domain.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders/products/{productId}")
    public ResponseEntity<ApiResponse<OrderResponse>> orderProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderRequest orderRequest,
            @PathVariable Long productId) {

        OrderResponse order = orderService.orderProduct(orderRequest, productId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("상품 구매가 완료되었습니다", order));
    }

    @PostMapping("/orders/lock/products/{productId}")
    public ResponseEntity<ApiResponse<OrderResponse>> orderProductExclusiveLock(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderRequest orderRequest,
            @PathVariable Long productId) {

        OrderResponse order = orderService.orderProductExclusiveLock(orderRequest, productId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("상품 구매가 완료되었습니다", order));
    }
}