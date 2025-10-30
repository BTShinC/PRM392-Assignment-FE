package com.example.prm392_assignment_food.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import java.util.List;

public class ItemManageAdapter extends RecyclerView.Adapter<ItemManageAdapter.ItemViewHolder> {

    private List<MenuItemResponse> itemList;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onUpdateClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ItemManageAdapter(Context context, List<MenuItemResponse> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_manage, parent, false);
        return new ItemViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MenuItemResponse currentItem = itemList.get(position);
        holder.tvItemName.setText(currentItem.getName());
        holder.tvItemPrice.setText(currentItem.getFormattedPrice());
        
        // NÂNG CẤP LỆNH GỌI GLIDE
        Glide.with(context)
                .load(currentItem.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh giữ chỗ
                .error(android.R.drawable.ic_menu_report_image) // Ảnh báo lỗi
                .into(holder.ivItemImage);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivItemImage;
        public TextView tvItemName;
        public TextView tvItemPrice;
        public ImageView ivUpdate;
        public ImageView ivDelete;

        public ItemViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            ivUpdate = itemView.findViewById(R.id.iv_update_item);
            ivDelete = itemView.findViewById(R.id.iv_delete_item);

            ivUpdate.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUpdateClick(position);
                    }
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
