package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {
    @SerializedName("orderId")
    public String orderId;

    @SerializedName("totalPrice")
    public float totalPrice;

    @SerializedName("status")
    public String orderStatus;

    @SerializedName("createdAt")
    public String createdAt;

    @SerializedName("orderItems")
    public List<OrderItemResponse> orderItems;

    public static class OrderItemResponse {
        @SerializedName("menuItemId")
        public String menuItemId;
        
        // Các trường khác không cần thiết cho màn hình này
    }
}