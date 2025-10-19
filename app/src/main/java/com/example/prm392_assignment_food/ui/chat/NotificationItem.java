package com.example.prm392_assignment_food.ui.chat;

public class NotificationItem {
    private final String name, action, time;
    private final int imageResId;

    public NotificationItem(String name, String action, String time, int imageResId) {
        this.name = name;
        this.action = action;
        this.time = time;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getAction() { return action; }
    public String getTime() { return time; }
    public int getImageResId() { return imageResId; }
}
