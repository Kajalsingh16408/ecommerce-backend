package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResponse {

    private String cartId;
    private String userId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
}