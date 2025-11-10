package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {

    @SerializedName("orderId")
    public String orderId;

    @SerializedName("users")
    public UserDto users;

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

        @SerializedName("quantity")
        public int quantity;

        @SerializedName("price")
        public float price;
    }

    public static class UserDto {
        @SerializedName("userId")
        public String userId;
        
        @SerializedName("name")
        public String name;

        @SerializedName("phone")
        public String phone;

        @SerializedName("address")
        public String address;
    }
}
