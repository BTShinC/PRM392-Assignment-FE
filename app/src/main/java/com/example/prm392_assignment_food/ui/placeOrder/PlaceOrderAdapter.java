package com.example.prm392_assignment_food.ui.placeOrder;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.ViewHolder> {

    private List<CartItemResponse> items;
    private final NumberFormat currencyFormatter; // Đối tượng định dạng tiền tệ

    public PlaceOrderAdapter(List<CartItemResponse> items) {
        this.items = items;

        // Khởi tạo đối tượng định dạng tiền tệ cho Việt Nam
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Tùy chỉnh ký hiệu tiền tệ từ "đ" thành " VND"
        if (formatter instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat) formatter;
            DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            symbols.setCurrencySymbol(" VND"); // Đặt ký hiệu là " VND" (có khoảng trắng ở đầu)
            decimalFormat.setDecimalFormatSymbols(symbols);
        }

        this.currencyFormatter = formatter;
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
            // Sử dụng đối tượng định dạng đã tạo
            holder.tvPrice.setText(currencyFormatter.format(totalPrice));
        }

        // Cải thiện: Dùng Glide để tải ảnh từ URL
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.halim) // Ảnh chờ
                .error(R.drawable.halim)       // Ảnh khi lỗi
                .into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

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
