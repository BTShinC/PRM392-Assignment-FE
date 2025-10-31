package com.example.prm392_assignment_food.ui.admin;

public class RunningOrder {
    private String id; // Order ID
    private String category;
    private String name;
    private String price;
    private String imageUrl;

    public RunningOrder(String id, String category, String name, String price, String imageUrl) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}