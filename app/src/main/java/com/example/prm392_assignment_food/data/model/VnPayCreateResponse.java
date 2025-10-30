package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

public class VnPayCreateResponse {
    @SerializedName("paymentUrl")
    private String paymentUrl;

    public String getPaymentUrl() {
        return paymentUrl;
    }
}
