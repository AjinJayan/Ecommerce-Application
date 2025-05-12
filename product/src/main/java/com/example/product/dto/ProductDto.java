package com.example.product.dto;

import com.example.product.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDto {
    private String name;
    private double price;
    private String description;
    private Category category;
}
