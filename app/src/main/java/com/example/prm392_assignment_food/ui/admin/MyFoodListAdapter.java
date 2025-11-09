package com.example.prm392_assignment_food.ui.admin;

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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.FoodViewHolder> {

    private final List<MenuItemResponse> menuItems;

    public MyFoodListAdapter(List<MenuItemResponse> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        MenuItemResponse menuItem = menuItems.get(position);
        holder.foodName.setText(menuItem.getName());
        holder.category.setText(menuItem.getCategoryName());
        // Format price to VND
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.price.setText(currencyFormatter.format(menuItem.getPrice()));

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(menuItem.getImageUrl())
                .placeholder(R.drawable.onboarding1) // Optional placeholder
                .into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void updateData(List<MenuItemResponse> newItems) {
        menuItems.clear();
        menuItems.addAll(newItems);
        notifyDataSetChanged();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, category, price;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.img_food);
            foodName = itemView.findViewById(R.id.tv_food_name);
            category = itemView.findViewById(R.id.tv_category);
            price = itemView.findViewById(R.id.tv_price);
        }
    }
}
