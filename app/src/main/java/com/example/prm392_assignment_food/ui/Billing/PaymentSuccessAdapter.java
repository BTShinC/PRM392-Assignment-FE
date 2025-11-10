package com.example.prm392_assignment_food.ui.Billing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// === THÊM IMPORT NÀY ===
import com.bumptech.glide.Glide;
// =======================

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PaymentSuccessAdapter extends RecyclerView.Adapter<PaymentSuccessAdapter.ViewHolder> {

    private List<CartItemResponse> items;
    private final NumberFormat currencyFormatter; // <-- THÊM DÒNG NÀY

    public PaymentSuccessAdapter(List<CartItemResponse> items) {
        this.items = items;

        // === THÊM KHỐI CODE NÀY (Giống hệt CartAdapter) ===
        // Khởi tạo đối tượng định dạng tiền tệ cho Việt Nam
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Tùy chỉnh ký hiệu tiền tệ từ "đ" thành " VND"
        if (formatter instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat) formatter;
            DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            // Đặt ký hiệu là " VND" (có khoảng trắng ở đầu)
            symbols.setCurrencySymbol(" VND");
            decimalFormat.setDecimalFormatSymbols(symbols);
        }
        this.currencyFormatter = formatter;
        // ================================================
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

        // Set tên món ăn (Đã đúng)
        holder.tvName.setText(item.getMenuItemName());

        // Set số lượng (Đã đúng)
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Tính và hiển thị giá (Cái này đã đúng)
        if (item.getUnitPrice() != null) {
            double totalPrice = item.getUnitPrice().doubleValue() * item.getQuantity();
            // === SỬA LỖI Ở ĐÂY ===
            // holder.tvPrice.setText(String.format(Locale.US, "$%.2f", totalPrice)); // <-- Dòng cũ
            holder.tvPrice.setText(currencyFormatter.format(totalPrice)); // <-- Dòng mới
            // ======================
        } else {
            // === SỬA LỖI Ở ĐÂY ===
            // holder.tvPrice.setText("$0.00"); // <-- Dòng cũ
            holder.tvPrice.setText(currencyFormatter.format(0)); // <-- Dòng mới
            // ======================
        }

        // === SỬA LỖI Ở ĐÂY ===
        // Sử dụng Glide để tải hình ảnh thật từ URL, giống như CartAdapter
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl()) // Lấy URL hình ảnh từ database
                .placeholder(R.drawable.halim) // (Tùy chọn) Hình ảnh chờ
                .error(R.drawable.halim)       // (Tùy chọn) Hình ảnh khi lỗi
                .into(holder.imgFood);
        // ======================
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
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}