package com.example.prm392_assignment_food.ui.placeOrder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.ViewHolder> {

    // Sửa: Bỏ 'final' để có thể cập nhật danh sách
    private List<CartItemResponse> items;

    public PlaceOrderAdapter(List<CartItemResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemResponse item = items.get(position);

        holder.tvName.setText(item.getMenuItemName());
        holder.tvQuantity.setText("x" + item.getQuantity());

        if (item.getUnitPrice() != null) {
            double totalPrice = item.getUnitPrice().doubleValue() * item.getQuantity();
            holder.tvPrice.setText(String.format(Locale.US, "$%.2f", totalPrice));
        }

        holder.imgFood.setImageResource(R.drawable.halim);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // --- THÊM VÀO: Phương thức để cập nhật dữ liệu ---
    public void updateItems(List<CartItemResponse> newItems) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged(); // Báo cho RecyclerView biết dữ liệu đã thay đổi
    }
    // --- KẾT THÚC THÊM ---

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
