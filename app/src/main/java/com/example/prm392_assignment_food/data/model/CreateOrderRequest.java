package com.example.prm392_assignment_food.data.model;

import java.util.List;
import java.util.UUID;

public class CreateOrderRequest {
    private UUID userId;

   // private PaymentMethod paymentMethod; chưa fix

    private List<OrderItemRequest> orderItemRequests;
}
