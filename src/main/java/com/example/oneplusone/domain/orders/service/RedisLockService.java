package com.example.oneplusone.domain.orders.service;

import com.example.oneplusone.domain.common.redis.LockService;
import com.example.oneplusone.domain.common.redis.RedissonLockService;
import com.example.oneplusone.domain.orders.dto.request.OrderRequest;
import com.example.oneplusone.domain.orders.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final OrderService orderService;
    private final LockService lockService;
    private final RedissonLockService redissonLockService;

    //redis lettuce 구현
    public OrderResponse orderProductLockService(OrderRequest orderRequest, Long productId, Long userId) {
        return lockService.execute(productId, () -> orderService.orderProduct(orderRequest, productId, userId));
    }

    public OrderResponse orderProductRedissonLockService(OrderRequest orderRequest, Long productId, Long userId) {
        return redissonLockService.execute(productId, () -> orderService.orderProduct(orderRequest, productId, userId));
    }
}
