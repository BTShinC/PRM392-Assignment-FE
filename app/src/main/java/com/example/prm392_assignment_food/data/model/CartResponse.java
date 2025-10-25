package com.example.prm392_assignment_food.data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CartResponse {
    private UUID cartId;
    private UUID userId;
    private String updatedAt;
    private int totalQuantity;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;

    // Getters
    public UUID getCartId() {
        return cartId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }
}
