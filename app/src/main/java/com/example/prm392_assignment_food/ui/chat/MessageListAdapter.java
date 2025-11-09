package com.example.prm392_assignment_food.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<Conversation> conversations; // <<< SỬA LỖI 1: Thay đổi kiểu dữ liệu của List
    private final OnConversationClickListener listener;

    // Thay đổi Interface để làm việc với Conversation
    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    // Thay đổi Constructor để nhận đúng kiểu dữ liệu
    public MessageListAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Hãy đảm bảo bạn có file layout item_message_user.xml với các ID đúng
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy đối tượng Conversation từ list
        Conversation conversation = conversations.get(position);
        holder.bind(conversation, listener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    // <<< SỬA LỖI 2: Thêm phương thức updateData mà MessageListActivity đang gọi >>>
    public void updateData(List<Conversation> newConversations) {
        this.conversations.clear();
        this.conversations.addAll(newConversations);
        notifyDataSetChanged(); // Báo cho RecyclerView biết dữ liệu đã thay đổi để vẽ lại
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Vui lòng kiểm tra lại ID trong file item_message_user.xml của bạn
        TextView name, lastMessage, time, unreadCount;

        ViewHolder(View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            name = itemView.findViewById(R.id.tvUserName);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            time = itemView.findViewById(R.id.tvTime);
            unreadCount = itemView.findViewById(R.id.tvUnreadCount);
        }

        // Phương thức bind để gán dữ liệu từ Conversation vào View
        void bind(final Conversation conversation, final OnConversationClickListener listener) {
            name.setText(conversation.getOtherUserName());
            lastMessage.setText(conversation.getLastMessageContent());
            time.setText(conversation.getLastMessageTime());

            if (conversation.getUnreadCount() > 0) {
                unreadCount.setVisibility(View.VISIBLE);
                unreadCount.setText(String.valueOf(conversation.getUnreadCount()));
            } else {
                unreadCount.setVisibility(View.GONE);
            }

            // Gán sự kiện click cho cả item
            itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
        }
    }
}
