package com.example.product.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long cartId;
    private Long userId;
    private Long productId;
    private Long quantity;
    private BigDecimal price;
    private ProductDto product;
}