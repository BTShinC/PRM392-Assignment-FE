package com.example.prm392_assignment_food.ui.chat;

public class MessageUser {
    private String name;
    private String lastMessage;
    private String time;
    private int unreadCount;

    public MessageUser(String name, String lastMessage, String time, int unreadCount) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unreadCount = unreadCount;
    }

    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public int getUnreadCount() { return unreadCount; }
}
