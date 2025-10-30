package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

public class VnPayCreateRequest {

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("amount")
    private long amount;

    @SerializedName("orderDescription")
    private String orderDescription;

    @SerializedName("bankCode")
    private String bankCode;

    @SerializedName("method")
    private String method;

    public VnPayCreateRequest(String orderId, long amount, String orderDescription, String bankCode) {
        this.orderId = orderId;
        this.amount = amount;
        this.orderDescription = orderDescription;
        this.bankCode = bankCode;
        this.method = "VNPAY"; // Giá trị này là cố định theo API
    }
}
