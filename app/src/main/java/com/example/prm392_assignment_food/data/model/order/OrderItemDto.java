package com.example.prm392_assignment_food.data.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderItemDto {
    private UUID orderItemId;

    private UUID menuItemId;

    private Integer quantity;

    private BigDecimal price;

    private LocalDateTime createdAt;

}
