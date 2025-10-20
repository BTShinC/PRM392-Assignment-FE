package com.example.prm392_assignment_food.data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CartResponse {
    private UUID cartId;
    private UUID userId;
    private LocalDateTime updatedAt;
    private int totalQuantity;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;
}
