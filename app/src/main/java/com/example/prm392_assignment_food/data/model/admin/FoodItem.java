package com.example.prm392_assignment_food.data.model.admin;

public class FoodItem {
    private String name;
    private String category;
    private float rating;
    private int reviews;
    private double price;
    private int imageResId;

    public FoodItem(String name, String category, float rating, int reviews, double price, int imageResId) {
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.reviews = reviews;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public float getRating() {
        return rating;
    }

    public int getReviews() {
        return reviews;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}
