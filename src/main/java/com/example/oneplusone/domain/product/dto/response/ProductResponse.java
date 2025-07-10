package com.example.oneplusone.domain.product.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponse {
    private Long id;

    private String productName;

    private String type;

    private Long price;

    private Long quantity;

    public ProductResponse(Long id, String name, String type, Long price, Long quantity) {
        this.id = id;
        this.productName = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }
}