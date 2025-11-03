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


import com.example.prm392_assignment_food.ui.admin.PopularItem;

import java.util.List;
import java.util.Locale;

public class PopularItemAdapter extends RecyclerView.Adapter<PopularItemAdapter.ViewHolder> {

    private final Context context;
    private final List<PopularItem> popularItems;

    public PopularItemAdapter(Context context, List<PopularItem> popularItems) {
        this.context = context;
        this.popularItems = popularItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PopularItem item = popularItems.get(position);
        holder.tvFoodName.setText(item.menuItem.getName());
        holder.tvQuantitySold.setText(String.format(Locale.getDefault(), "Sold: %d", item.quantity));
        Glide.with(context).load(item.menuItem.getImageUrl()).into(holder.ivFoodImage);
    }

    @Override
    public int getItemCount() {
        return popularItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName;
        TextView tvQuantitySold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantitySold = itemView.findViewById(R.id.tvQuantitySold);
        }
    }
}
