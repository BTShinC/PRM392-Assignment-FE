package com.example.prm392_assignment_food.ui.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotificationResponse> items;
    private OnItemClickListener listener;
    private final Context context;

    public interface OnItemClickListener {
        void onItemClick(NotificationResponse notification);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NotificationAdapter(Context context, List<NotificationResponse> items) {
        this.context = context;
        this.items = items;
    }

    public void updateData(List<NotificationResponse> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationResponse item = items.get(position);

        // --- Gán dữ liệu cơ bản ---
        holder.tvAction.setText(item.getContent());
        holder.tvName.setText("Thông báo hệ thống");

        // <<< CẬP NHẬT: Định dạng ngày giờ đầy đủ >>>
        holder.tvTime.setText(formatDateTime(item.getCreatedAt()));

        // --- Logic thay đổi màu sắc và icon dựa trên type ---
        int indicatorColorRes = R.color.notification_general;
        int avatarIconRes = R.drawable.logo;

        NotificationType type = item.getType();
        if (type != null) {
            switch (type) {
                case ORDER_COMPLETED:
                case ORDER_DELIVERED:
                case ORDER_PAID:
                    indicatorColorRes = R.color.notification_success;
                    avatarIconRes = R.drawable.ic_order_success;
                    break;
                case ORDER_CANCELLED:
                case ORDER_PAYMENT_FAILED:
                    indicatorColorRes = R.color.notification_error;
                    avatarIconRes = R.drawable.ic_order_error;
                    break;
                case ORDER_CONFIRMED:
                case ORDER_SHIPPING:
                    indicatorColorRes = R.color.notification_info;
                    avatarIconRes = R.drawable.ic_order_info;
                    break;
                case ORDER_AWAITING_PAYMENT:
                    indicatorColorRes = R.color.notification_warning;
                    avatarIconRes = R.drawable.ic_order_warning;
                    break;
                case GENERAL:
                default:
                    // Giữ giá trị mặc định đã khai báo ở trên
                    break;
            }
        }

        // Áp dụng màu và icon đã chọn vào Views
        holder.indicator.setBackgroundColor(ContextCompat.getColor(context, indicatorColorRes));
        holder.imgAvatar.setImageResource(avatarIconRes);

        // --- Bắt sự kiện click ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    /**
     * <<< HÀM MỚI: Định dạng chuỗi thời gian từ API thành "HH:mm dd/MM/yyyy" >>>
     * @param isoDateTime Chuỗi thời gian theo định dạng ISO (ví dụ: "2025-11-07T07:33:08.383669")
     * @return Chuỗi đã được định dạng hoặc chuỗi gốc nếu có lỗi.
     */
    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return "";
        }
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, inputFormatter);
            return dateTime.format(outputFormatter);
        } catch (DateTimeParseException e) {
            Log.e("NotificationAdapter", "Error parsing date: " + isoDateTime, e);
            // Nếu lỗi, trả về chuỗi gốc để debug
            return isoDateTime;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood, imgAvatar;
        TextView tvName, tvAction, tvTime;
        View indicator;

        ViewHolder(View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvName);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvTime = itemView.findViewById(R.id.tvTime);
            indicator = itemView.findViewById(R.id.indicator);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}