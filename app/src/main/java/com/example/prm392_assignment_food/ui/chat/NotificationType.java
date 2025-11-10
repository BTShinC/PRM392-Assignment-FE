package com.example.prm392_assignment_food.ui.chat;

import com.google.gson.annotations.SerializedName;

public enum NotificationType {
    @SerializedName("ORDER_AWAITING_PAYMENT")
    ORDER_AWAITING_PAYMENT,

    @SerializedName("ORDER_PAID")
    ORDER_PAID,

    @SerializedName("ORDER_PAYMENT_FAILED")
    ORDER_PAYMENT_FAILED,

    @SerializedName("ORDER_CONFIRMED")
    ORDER_CONFIRMED,

    @SerializedName("ORDER_SHIPPING")
    ORDER_SHIPPING,

    @SerializedName("ORDER_DELIVERED")
    ORDER_DELIVERED,

    @SerializedName("ORDER_COMPLETED")
    ORDER_COMPLETED,

    @SerializedName("ORDER_CANCELLED")
    ORDER_CANCELLED,

    @SerializedName("GENERAL")
    GENERAL
}
