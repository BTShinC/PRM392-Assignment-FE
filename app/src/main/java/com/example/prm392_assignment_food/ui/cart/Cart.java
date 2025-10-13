package com.example.prm392_assignment_food.ui.cart;

public class Cart {
    private String name;
    private String description;
    private int price;
    private int imageResource;
    private String time;
    private int quantity;

    public Cart(String name, String description, int price, int imageResource, String time, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.time = time;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTime() {
        return time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
