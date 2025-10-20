package com.example.prm392_assignment_food.data.model;

import java.util.UUID;

public class CartItemRequest {

    private UUID menuItemId;

    private int quantity;

    public CartItemRequest(UUID menuItemId, int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;

    }
    public UUID getMenuItemId() {
        return menuItemId;
    }
    public int getQuantity() {
        return quantity;
    }
}
