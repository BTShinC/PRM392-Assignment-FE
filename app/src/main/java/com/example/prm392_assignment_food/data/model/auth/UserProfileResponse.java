package com.example.prm392_assignment_food.data.model.auth;

public class UserProfileResponse {
    private int status;
    private String message;
    private User data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }
}
