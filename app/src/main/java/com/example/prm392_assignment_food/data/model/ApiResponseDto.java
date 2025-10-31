package com.example.prm392_assignment_food.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Generic wrapper for standard API responses that follow the {status, message, data} structure.
 * @param <T> The type of the data payload.
 */
public class ApiResponseDto<T> {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}