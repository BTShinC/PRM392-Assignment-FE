package com.example.prm392_assignment_food.ui.customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    
    private Context context;
    private List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        
        // Bind data to views
        holder.tvFoodName.setText(food.getName());
        holder.tvCategory.setText(food.getCategory());
        holder.tvPrice.setText(food.getPrice());
        holder.tvRating.setText(String.valueOf(food.getRating()));
        holder.tvReviews.setText("(" + food.getReviewCount() + " Review)");
        holder.imgFood.setImageResource(food.getImageResource());
        
        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("food_name", food.getName());
            intent.putExtra("food_price", food.getPrice());
            intent.putExtra("food_category", food.getCategory());
            intent.putExtra("food_rating", food.getRating());
            intent.putExtra("food_reviews", food.getReviewCount());
            intent.putExtra("food_image", food.getImageResource());
            intent.putExtra("food_location", food.getLocation());
            intent.putExtra("food_description", food.getDescription());
            intent.putExtra("food_delivery_type", food.getDeliveryType());
            context.startActivity(intent);
        });
        
        // Set click listener for pick up button
        holder.btnPickUp.setOnClickListener(v -> {
            // Handle pick up action
        });
        
        // Set click listener for more options
        holder.btnMore.setOnClickListener(v -> {
            // Handle more options action
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood, btnMore;
        TextView tvFoodName, tvCategory, tvPrice, tvRating, tvReviews, btnPickUp;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imgFood = itemView.findViewById(R.id.img_food);
            btnMore = itemView.findViewById(R.id.btn_more);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvReviews = itemView.findViewById(R.id.tv_reviews);
            btnPickUp = itemView.findViewById(R.id.btn_pick_up);
        }
    }
}
