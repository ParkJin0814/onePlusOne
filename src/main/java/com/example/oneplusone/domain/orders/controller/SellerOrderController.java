package com.example.oneplusone.domain.orders.controller;

import com.example.oneplusone.domain.common.dto.ApiResponse;
import com.example.oneplusone.domain.common.dto.PagedResponse;
import com.example.oneplusone.domain.common.security.UserDetailsImpl;
import com.example.oneplusone.domain.orders.dto.response.OrderResponse;
import com.example.oneplusone.domain.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerOrderController {
    private final OrderService orderService;

    @GetMapping("/orders/products/{productId}")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getBuyersByProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<OrderResponse> orders = orderService.getBuyersByProduct(productId, userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.ok("구매 목록이 조회되었습니다.", orders));
    }
}
