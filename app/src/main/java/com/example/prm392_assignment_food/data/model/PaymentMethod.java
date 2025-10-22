package com.example.prm392_assignment_food.data.model;

public enum PaymentMethod {
    VNPAY("VNPay Gateway"),
    MOMO("Momo E-Wallet"),
    ZALOPAY("ZaloPay E-Wallet"),
    COD("Cash On Delivery"),
    BANK_TRANSFER("Bank Transfer");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public static PaymentMethod fromString(String method) {
        if (method == null) return null;
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid payment method: " + method);
        }
    }
}
