package com.example.oneplusone.domain.product.dto.response;

import com.example.oneplusone.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSellerResponse {

    private final Long id;
    private final Long sellerId;
    private final String productName;
    private final String type;
    private final Long price;
    private final Long quantity;

    public static ProductSellerResponse from(Product product) {
        return new ProductSellerResponse(
                product.getId(),
                product.getUser().getId(),
                product.getName(),
                product.getType(),
                product.getPrice(),
                product.getQuantity()
        );
    }

}