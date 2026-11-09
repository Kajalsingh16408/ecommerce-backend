package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItemResponse {

    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal lineTotal;
}