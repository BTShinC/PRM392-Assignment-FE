package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp generic đại diện cho cấu trúc phản hồi chuẩn từ API.
 * Bao gồm status, message, và dữ liệu chính (data).
 * @param <T> Kiểu dữ liệu của đối tượng 'data'.
 */
public class ApiResponse<T> {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
