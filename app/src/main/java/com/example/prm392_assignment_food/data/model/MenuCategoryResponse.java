package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Menu Category Response from the backend
 */
public class MenuCategoryResponse {
    @SerializedName("categoryId") // SỬA Ở ĐÂY: từ "id" -> "categoryId"
    private String categoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    public MenuCategoryResponse() {
    }

    // Getters
    public String getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
