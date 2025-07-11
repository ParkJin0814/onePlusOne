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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SearchService searchService;
    @Transactional
    public OrderResponse orderProduct(OrderRequest orderRequest, Long productId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));

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

    public Page<OrderResponse> getBuyersByProduct(Long productId, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(productId).orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getUser().equals(user)) throw new BaseException(ErrorCode.PRODUCT_IS_NOT_YOURS);

        Page<Order> orders = orderRepository.findByProductId(productId, pageable);

        return orders.map(OrderResponse::new);
    }
}
