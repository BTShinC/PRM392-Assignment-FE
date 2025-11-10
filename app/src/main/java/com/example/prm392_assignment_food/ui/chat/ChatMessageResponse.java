package com.example.prm392_assignment_food.ui.chat;

import java.util.UUID;

public class ChatMessageResponse {
    private UUID messageId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private boolean isRead;
    private String createdAt;

    public ChatMessageResponse() {
    }

    public ChatMessageResponse(UUID messageId, UUID senderId, UUID receiverId, String content, boolean isRead, String createdAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getMessageId() { return messageId; }
    public UUID getSenderId() { return senderId; }
    public UUID getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setMessageId(UUID messageId) { this.messageId = messageId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }
    public void setContent(String content) { this.content = content; }
    public void setRead(boolean read) { isRead = read; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
