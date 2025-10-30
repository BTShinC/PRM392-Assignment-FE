package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID; // Sử dụng java.util.UUID

public class CreateOrderRequest {

    @SerializedName("userId")
    private UUID userId;

    @SerializedName("paymentMethod")
    private PaymentMethod paymentMethod;

    @SerializedName("orderItemRequests")
    private List<OrderItemRequest> orderItemRequests;
    
    public CreateOrderRequest(UUID userId, PaymentMethod paymentMethod, List<OrderItemRequest> orderItemRequests) {
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.orderItemRequests = orderItemRequests;
    }
}