package com.example.oneplusone.domain.orders.dto.response;

import com.example.oneplusone.domain.orders.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private Long quantity;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.productId = order.getProduct().getId();
        this.quantity = order.getQuantity();
    }
}
