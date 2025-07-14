package com.example.oneplusone.domain.orders.service;

import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.orders.dto.request.OrderRequest;
import com.example.oneplusone.domain.orders.dto.response.OrderResponse;
import com.example.oneplusone.domain.orders.entity.Order;
import com.example.oneplusone.domain.orders.repository.OrderRepository;
import com.example.oneplusone.domain.product.entity.Product;
import com.example.oneplusone.domain.product.repository.ProductRepository;
import com.example.oneplusone.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SearchService searchService;
    private final RedissonClient redissonClient;

    @Transactional
    public OrderResponse orderProduct(OrderRequest orderRequest, Long productId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        //q
        Product product = productRepository.findByIdWithLock(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));

        Long quantity = orderRequest.getQuantity();
        if (product.getQuantity() < quantity) {
            throw new BaseException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        Long quantityAfter = product.getQuantity() - quantity;
        product.setQuantity(quantityAfter);
        // 캐시 동기화가 맞지 않는 상황이기 때문에 캐시를 삭제
        searchService.cacheEviction(productId);

        Order order = new Order(user, product, quantity);
        orderRepository.save(order);

        return new OrderResponse(order);
    }

    // redisson
    @Transactional
    public OrderResponse orderProductV2(OrderRequest orderRequest, Long productId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        String lockName = "USER" + user.getId();
        RLock rLock = redissonClient.getLock(lockName);

        long waitTime = 5L;
        long leaseTime = 3L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            boolean available = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if(!available){
                throw new BaseException(ErrorCode.LOCK_NOT_AVAILABLE);
            }
            //=== 락 획득 후 로직 수행 ===
            Product product = productRepository.findByIdWithLock(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));

            Long quantity = orderRequest.getQuantity();
            if (product.getQuantity() < quantity) {
                throw new BaseException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            Long quantityAfter = product.getQuantity() - quantity;
            product.setQuantity(quantityAfter);
            // 캐시 동기화가 맞지 않는 상황이기 때문에 캐시를 삭제
            searchService.cacheEviction(productId);

            Order order = new Order(user, product, quantity);
            orderRepository.save(order);

            return new OrderResponse(order);
            // === 로직 수행 완료 ===
        }catch (InterruptedException e){
            //락을 얻으려고 시도하다가 인터럽트를 받았을 때 발생하는 예외
            throw new BaseException(ErrorCode.LOCK_INTERRUPTED_ERROR);
        }finally{
            try{
                rLock.unlock();
                log.info("unlock complete: {}", rLock.getName());
            }catch (IllegalMonitorStateException e){
                //이미 종료된 락일 때 발생하는 예외
                throw new BaseException(ErrorCode.UNLOCKING_A_LOCK_WHICH_IS_NOT_LOCKED);
            }
        }
    }

    public Page<OrderResponse> getBuyersByProduct(Long productId, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getUser().equals(user)) throw new BaseException(ErrorCode.PRODUCT_IS_NOT_YOURS);

        Page<Order> orders = orderRepository.findByProductId(productId, pageable);

        return orders.map(OrderResponse::new);
    }
}
