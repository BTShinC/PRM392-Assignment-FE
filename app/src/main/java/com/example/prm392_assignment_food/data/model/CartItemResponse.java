package com.example.prm392_assignment_food.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * POJO class để đại diện cho một item trong giỏ hàng nhận về từ API.
 */
public class CartItemResponse implements Serializable {
    private UUID cartItemId;
    private UUID menuItemId;
    private String menuItemName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal lineTotal;

    private String imageUrl;

    public String getImageUrl() { // Add this getter
        return imageUrl;
    }

    // ... other setters

    public void setImageUrl(String imageUrl) { // Add this setter
        this.imageUrl = imageUrl;
    }

    // Constructor rỗng cần thiết cho Gson
    public CartItemResponse() {
    }

    // Getters
    public UUID getCartItemId() {
        return cartItemId;
    }

    public UUID getMenuItemId() {
        return menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    // Setters
    public void setCartItemId(UUID cartItemId) {
        this.cartItemId = cartItemId;
    }

    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
