package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

public class MenuItemRequest {

    @SerializedName("categoryId")
    private String categoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    public MenuItemRequest(String categoryId, String name, String description, double price, boolean isAvailable) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // Getters and setters
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
