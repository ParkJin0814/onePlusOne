package com.example.oneplusone.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String productName;

    @NotBlank(message = "상품종류는 필수입니다.")
    private String type;

    @NotNull(message = "상품가격은 필수입니다.")
    private Long price;

    @NotNull(message = "상품수량은 필수입니다.")
    private Long quantity;

}