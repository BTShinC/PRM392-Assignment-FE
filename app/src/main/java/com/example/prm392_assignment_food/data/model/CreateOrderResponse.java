package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CreateOrderResponse {
    // Tên trường "orderId" phải khớp với response JSON từ backend
    @SerializedName("orderId")
    private String orderId;

    // Tên trường "totalPrice" cũng phải khớp
    @SerializedName("totalPrice")
    private BigDecimal totalPrice;

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
