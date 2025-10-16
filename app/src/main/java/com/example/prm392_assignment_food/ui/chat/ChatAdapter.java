package com.example.prm392_assignment_food.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> messages;
    private static final int TYPE_SENDER = 1;
    private static final int TYPE_RECEIVER = 2;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSender() ? TYPE_SENDER : TYPE_RECEIVER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);

        if (holder instanceof SenderViewHolder) {
            SenderViewHolder vh = (SenderViewHolder) holder;
            vh.tvMessage.setText(msg.getContent());

            // Hiển thị thời gian nếu có
            if (msg.getTime() != null) {
                vh.tvTime.setText(timeFormat.format(msg.getTime()));
            } else {
                vh.tvTime.setText("");
            }

            // Avatar có thể thay bằng ảnh thật (nếu có)
            vh.imgSender.setImageResource(R.drawable.ic_person_placeholder);

            // Hiển thị tick ✅ nếu là tin người gửi
            vh.icTick.setVisibility(View.VISIBLE);

        } else if (holder instanceof ReceiverViewHolder) {
            ReceiverViewHolder vh = (ReceiverViewHolder) holder;
            vh.tvMessage.setText(msg.getContent());

            if (msg.getTime() != null) {
                vh.tvTime.setText(timeFormat.format(msg.getTime()));
            } else {
                vh.tvTime.setText("");
            }

            vh.imgReceiver.setImageResource(R.drawable.ic_person_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ----- ViewHolder cho người gửi -----
    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        ImageView imgSender, icTick;

        SenderViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageSender);
            tvTime = itemView.findViewById(R.id.tvTimeSender);
            imgSender = itemView.findViewById(R.id.imgSender);
            icTick = itemView.findViewById(R.id.icTick);
        }
    }

    // ----- ViewHolder cho người nhận -----
    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        ImageView imgReceiver;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageReceiver);
            tvTime = itemView.findViewById(R.id.tvTimeReceiver);
            imgReceiver = itemView.findViewById(R.id.imgReceiver);
        }
    }
}
