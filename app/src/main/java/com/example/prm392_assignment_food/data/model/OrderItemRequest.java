package com.example.prm392_assignment_food.data.model;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemRequest {
    private UUID menuItemId;

    private Integer quantity;

    private BigDecimal price;

    public OrderItemRequest(UUID menuItemId, Integer quantity, BigDecimal price) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;

    }
    // Getters and setters
    public UUID getMenuItemId() {
        return menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
