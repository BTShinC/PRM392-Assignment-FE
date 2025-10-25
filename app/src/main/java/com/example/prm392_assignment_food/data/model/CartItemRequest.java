package com.example.prm392_assignment_food.data.model;

import java.util.UUID;

/**
 * POJO class để gửi yêu cầu thêm/cập nhật một item trong giỏ hàng.
 */
public class CartItemRequest {
    private UUID menuItemId;
    private int quantity;

    // Constructor
    public CartItemRequest(UUID menuItemId, int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    // Getters
    public UUID getMenuItemId() {
        return menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
