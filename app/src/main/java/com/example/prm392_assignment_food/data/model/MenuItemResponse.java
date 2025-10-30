package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import java.io.Serializable;


public class MenuItemResponse implements Serializable {
    @SerializedName("menuItemId")
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

    @SerializedName("isAvailable")
    private Boolean available;
    @SerializedName("rating")
    private int rating;
    @SerializedName("comment")
    private String comment;

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
    public String getComment() { return comment; }

    public int getRating() { return rating; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setAvailable(Boolean available) { this.available = available; }


    public void setComment(String comment) { this.comment = comment; }

    public void setRating(int rating) { this.rating = rating; }

    // Helper method
    public String getFormattedPrice() {
        if (price == null) return "0 VND";
        
        // Tạo DecimalFormat với dấu chấm phân cách hàng nghìn
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        
        return formatter.format(price.longValue()) + " VND";
    }
}
