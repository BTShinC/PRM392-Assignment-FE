package com.example.prm392_assignment_food.data.model;

/**
 * POJO class để gửi yêu cầu cập nhật số lượng của một item trong giỏ hàng.
 */
public class UpdateQuantityRequest {
    private int quantity;

    public UpdateQuantityRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
