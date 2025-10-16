package com.example.prm392_assignment_food.ui.customer;

import java.util.List;

public class Food {
    private String name;
    private String price;
    private String category;
    private float rating;
    private int reviewCount;
    private int imageResource;
    private String location;
    private List<String> ingredients;
    private String description;
    private String deliveryType;

    // Constructor
    public Food(String name, String price, String category, float rating, int reviewCount, 
                int imageResource, String location, List<String> ingredients, String description, String deliveryType) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageResource = imageResource;
        this.location = location;
        this.ingredients = ingredients;
        this.description = description;
        this.deliveryType = deliveryType;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public int getImageResource() { return imageResource; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }
}
