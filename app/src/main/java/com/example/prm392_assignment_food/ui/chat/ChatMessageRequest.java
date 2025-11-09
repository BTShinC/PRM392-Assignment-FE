package com.example.prm392_assignment_food.ui.chat;

import java.util.UUID;

public class ChatMessageRequest {
    private final UUID senderId;
    private final UUID receiverId;
    private final String content;

    public ChatMessageRequest(UUID senderId, UUID receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }
}

