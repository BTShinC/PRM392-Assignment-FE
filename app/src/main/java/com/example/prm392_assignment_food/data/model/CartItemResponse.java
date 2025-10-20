package com.example.prm392_assignment_food.data.model;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItemResponse {
    private UUID cartItemId;
    private UUID menuItemId;
    private String menuItemName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal lineTotal;
}
