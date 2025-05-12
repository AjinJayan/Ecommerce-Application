package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {
    // private Long cartId;
    // private Long userId;
    private Long productId;
    private Long quantity;
}
