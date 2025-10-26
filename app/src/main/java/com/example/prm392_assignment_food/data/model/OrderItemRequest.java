package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal; // Sử dụng BigDecimal cho giá tiền để đảm bảo chính xác
import java.util.UUID;

public class OrderItemRequest {

    @SerializedName("menuItemId")
    private UUID menuItemId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private BigDecimal price;


    public OrderItemRequest(UUID menuItemId, int quantity, BigDecimal price) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
    }
}