package com.example.prm392_assignment_food.data.model;

public class ResetPasswordRequest {
    private String email;
    private String password;
    private String otp;

    public ResetPasswordRequest(String email, String password, String otp) {
        this.email = email;
        this.password = password;
        this.otp = otp;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
