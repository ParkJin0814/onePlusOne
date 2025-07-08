package com.example.oneplusone.domain.product.dto.response;

import lombok.Getter;

@Getter
public class ProductResponse {
    private final Long id;

    private final String productName;

    private final String type;

    private final Long price;

    private final Long quantity;

    public ProductResponse(Long id, String name, String type, Long price, Long quantity) {
        this.id = id;
        this.productName = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }
}
