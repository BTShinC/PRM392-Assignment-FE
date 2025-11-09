package com.example.prm392_assignment_food.data.model;

public class ResponseDto <T> {

    private int status;

    private String message;

    private T data;

    // Getter for status
    public int getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(int status) {
        this.status = status;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter for data
    public T getData() {
        return data;
    }

    // Setter for data
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Helper method to quickly check if the API call was successful.
     * Assumes a status code of 200 indicates success.
     * @return true if status is 200, false otherwise.
     */
    public boolean isSuccess() {
        return status == 200;
    }
}
