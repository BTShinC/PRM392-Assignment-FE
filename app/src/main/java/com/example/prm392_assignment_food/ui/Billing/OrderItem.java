package com.example.prm392_assignment_food.ui.Billing;

public class OrderItem {
    private String imageUrl; // Changed from int imageResId
    private String name;
    private double price;
    private String size;
    private int quantity;

    // Constructor updated to accept imageUrl as a String
    public OrderItem(String imageUrl, String name, double price, String size, int quantity) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
    }

    // Getter for the image URL
    public String getImageUrl() {
        return imageUrl;
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
