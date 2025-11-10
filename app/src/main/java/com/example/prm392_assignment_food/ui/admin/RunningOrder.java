package com.example.prm392_assignment_food.ui.admin;

import java.io.Serializable;

public class RunningOrder implements Serializable {
    private String id;
    private String category;
    private String name;
    private double price;
    private String imageUrl;
    private String status;

    public RunningOrder(String id, String category, String name, double price, String imageUrl, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCategory(String category) { this.category = category; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setStatus(String status) { this.status = status; }
}
