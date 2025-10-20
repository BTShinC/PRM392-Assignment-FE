package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class cho Menu Item Response từ backend
 * Map với MenuItemResponse từ Spring Boot controller
 */
public class MenuItemResponse {
    @SerializedName("menuItemId")  // Backend trả về "menuItemId", không phải "id"
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private Double price;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("categoryId")
    private String categoryId;
    
    @SerializedName("categoryName")
    private String categoryName;
    
    @SerializedName("isAvailable")  // Backend trả về "isAvailable", không phải "available"
    private Boolean available;
    
    // Backend KHÔNG trả về rating và reviewCount, set default values
    private Float rating = 0f;
    private Integer reviewCount = 0;

    public MenuItemResponse() {
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public Boolean getAvailable() { return available; }
    public Float getRating() { return rating; }
    public Integer getReviewCount() { return reviewCount; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setAvailable(Boolean available) { this.available = available; }
    public void setRating(Float rating) { this.rating = rating; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    // Helper method
    public String getFormattedPrice() {
        return price != null ? String.format("%.0f", price) + " VND" : "0 VND";
    }
}

