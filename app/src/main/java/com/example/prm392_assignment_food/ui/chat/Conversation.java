package com.example.prm392_assignment_food.ui.chat;

import java.util.UUID;

public class Conversation {
    private UUID otherUserId;
    private String otherUserName; // Sẽ cần lấy thông tin user sau
    private String lastMessageContent;
    private String lastMessageTime;
    private int unreadCount;

    // Getters and Setters
    public UUID getOtherUserId() { return otherUserId; }
    public void setOtherUserId(UUID otherUserId) { this.otherUserId = otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }
    public String getLastMessageContent() { return lastMessageContent; }
    public void setLastMessageContent(String lastMessageContent) { this.lastMessageContent = lastMessageContent; }
    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
