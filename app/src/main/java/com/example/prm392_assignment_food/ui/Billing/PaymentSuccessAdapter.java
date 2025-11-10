package com.example.prm392_assignment_food.ui.Billing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;

import java.util.List;

public class PaymentSuccessAdapter extends RecyclerView.Adapter<PaymentSuccessAdapter.ViewHolder> {

    private final List<CartItemResponse> items;

    public PaymentSuccessAdapter(List<CartItemResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_success, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemResponse item = items.get(position);

        Glide.with(holder.itemView.getContext())
             .load(item.getImageUrl()) // Corrected
             .into(holder.imgFood);
             
        holder.tvName.setText(item.getMenuItemName()); // Corrected
        holder.tvPrice.setText(String.format("%,.0f đ", item.getUnitPrice().doubleValue())); // Corrected
        holder.tvSize.setText("Vừa"); // Assuming "Vừa" is the default size
        holder.tvQuantity.setText(String.valueOf(item.getQuantity())); // Corrected
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName, tvPrice, tvSize, tvQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
