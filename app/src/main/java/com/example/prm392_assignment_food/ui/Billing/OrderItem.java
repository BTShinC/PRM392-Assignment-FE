package com.example.prm392_assignment_food.ui.Billing;

public class OrderItem {
    private int imageResId;
    private String name;
    private double price;
    private String size;
    private int quantity;

    public OrderItem(int imageResId, String name, double price, String size, int quantity) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }
}
