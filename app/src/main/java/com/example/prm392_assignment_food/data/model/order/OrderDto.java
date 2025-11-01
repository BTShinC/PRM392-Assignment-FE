package com.example.prm392_assignment_food.data.model.order;

import com.example.prm392_assignment_food.data.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    private UUID orderId;

    private UUID paymentId;

    private UserDto users;

    private BigDecimal totalPrice;

    private OrderStatus status;

    private PaymentMethod paymentMethod;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemDto> orderItems;
}
