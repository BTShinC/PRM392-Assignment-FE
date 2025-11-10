package com.example.prm392_assignment_food.ui.chat;

import com.google.gson.annotations.SerializedName;

public class NotificationResponse {

    @SerializedName("notificationId")
    private String notificationId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("content")
    private String content;

    @SerializedName("type")
    private NotificationType type;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter và Setter đã được cập nhật cho 'type'
    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
