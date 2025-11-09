package com.example.prm392_assignment_food.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessageResponse> messages;
    private final UUID currentUserId;

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    public ChatAdapter(List<ChatMessageResponse> messages, UUID currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageResponse message = messages.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENDER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageResponse message = messages.get(position);
        if (getItemViewType(position) == VIEW_TYPE_SENDER) {
            ((SenderViewHolder) holder).bind(message);
        } else {
            ((ReceiverViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Thêm các hàm helper để cập nhật dữ liệu
    public void addMessage(ChatMessageResponse message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessageResponse> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        // Sắp xếp tin nhắn theo thời gian tăng dần
        Collections.sort(messages, (m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
        notifyDataSetChanged();
    }

    // ViewHolder cho tin nhắn gửi đi
    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageSender, tvTimeSender;

        SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageSender = itemView.findViewById(R.id.tvMessageSender);
            tvTimeSender = itemView.findViewById(R.id.tvTimeSender);
        }

        void bind(ChatMessageResponse message) {
            tvMessageSender.setText(message.getContent());
            tvTimeSender.setText(formatTime(message.getCreatedAt()));
        }
    }

    // ViewHolder cho tin nhắn nhận được
    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageReceiver, tvTimeReceiver;

        ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageReceiver = itemView.findViewById(R.id.tvMessageReceiver);
            tvTimeReceiver = itemView.findViewById(R.id.tvTimeReceiver);
        }

        void bind(ChatMessageResponse message) {
            tvMessageReceiver.setText(message.getContent());
            tvTimeReceiver.setText(formatTime(message.getCreatedAt()));
        }
    }

    // Hàm tiện ích để format thời gian
    private static String formatTime(String isoDateTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime);
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return "";
        }
    }
}