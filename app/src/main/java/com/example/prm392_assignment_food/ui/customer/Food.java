package com.example.prm392_assignment_food.ui.customer;

public class Food {
    private String id;
    private String name;
    private String price;
    private String category;
    private String imageUrl; // Thay đổi từ int imageResource
    private String location;
    private String description;

    // Constructor đã được cập nhật
    public Food(String id, String name, String price, String category,
                String imageUrl, String location, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl; // Cập nhật
        this.location = location;
        this.description = description;
    }

    // Getters and Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }


    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Cập nhật getter và setter cho imageUrl
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
