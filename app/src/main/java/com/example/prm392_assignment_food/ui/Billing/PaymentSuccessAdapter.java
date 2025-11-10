// File: com/example/prm392_assignment_food/ui/Billing/PaymentSuccessAdapter.java

package com.example.prm392_assignment_food.ui.Billing;

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

public class PaymentSuccessAdapter extends RecyclerView.Adapter<PaymentSuccessAdapter.ViewHolder> {

    private List<CartItemResponse> items;

    public PaymentSuccessAdapter(List<CartItemResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // SỬ DỤNG LAYOUT MỚI: item_order_success
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_success, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemResponse item = items.get(position);

        // Set tên món ăn (SỬA Ở ĐÂY)
        // Bây giờ holder.tvName đã tồn tại
        holder.tvName.setText(item.getMenuItemName());

        // Set số lượng (Cái này đã đúng)
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Tính và hiển thị giá (Cái này đã đúng)
        if (item.getUnitPrice() != null) {
            double totalPrice = item.getUnitPrice().doubleValue() * item.getQuantity();
            holder.tvPrice.setText(String.format(Locale.US, "$%.2f", totalPrice));
        } else {
            holder.tvPrice.setText("$0.00");
        }

        // Set hình ảnh (SỬA Ở ĐÂY)
        // Bây giờ holder.imgFood đã tồn tại
        holder.imgFood.setImageResource(R.drawable.halim);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Phương thức cập nhật dữ liệu nếu cần
    public void updateItems(List<CartItemResponse> newItems) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Đặt tên biến khớp với ID trong XML
        ImageView imgFood;
        TextView tvName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ views từ item_order_success.xml bằng đúng ID
            imgFood = itemView.findViewById(R.id.imgFood); // <-- Sửa từ ivFoodImage và R.id.ivFoodImage
            tvName = itemView.findViewById(R.id.tvName); // <-- Sửa từ tvFoodName và R.id.tvFoodName

            // Hai dòng này của bạn đã đúng rồi
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}