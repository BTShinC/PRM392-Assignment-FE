package com.example.prm392_assignment_food.ui.chat;

import java.util.Date;

public class Message {
    private String content;
    private boolean isSender;
    private Date time;

    public Message(String content, boolean isSender) {
        this.content = content;
        this.isSender = isSender;
        this.time = new Date(); // mặc định là thời gian hiện tại
    }

    public String getContent() {
        return content;
    }

    public boolean isSender() {
        return isSender;
    }

    public Date getTime() {
        return time;
    }
}
