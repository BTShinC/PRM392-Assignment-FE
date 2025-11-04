package com.example.prm392_assignment_food.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.admin.FoodItem;
import java.util.List;
import java.util.Locale;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.FoodViewHolder> {

    private final List<FoodItem> foodItems;

    public MyFoodListAdapter(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.category.setText(foodItem.getCategory());
        // Rating & reviews removed per requirement
        holder.price.setText(String.format(Locale.US, "$%.0f", foodItem.getPrice()));
        holder.foodImage.setImageResource(foodItem.getImageResId());
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
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
