package com.example.prm392_assignment_food.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Cart> cartList;
    private CartManager cartManager;

    // Interface để thông báo cho Activity khi giỏ hàng thay đổi
    public interface OnCartChangeListener {
        void onCartChanged();
    }

    private OnCartChangeListener cartChangeListener;

    // Constructor nhận vào danh sách và listener
    public CartAdapter(List<Cart> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.cartChangeListener = listener;
        this.cartManager = CartManager.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item từ file layout list_item_cart.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Lấy item hiện tại
        Cart currentItem = cartList.get(position);

        // Gán dữ liệu lên các view
        holder.itemImage.setImageResource(currentItem.getImageResource());
        holder.itemName.setText(currentItem.getName());
        holder.itemSize.setText(currentItem.getDescription()); // Giả sử description là size
        holder.itemPrice.setText(String.format(Locale.US, "$%d", currentItem.getPrice()));
        holder.itemQuantity.setText(String.valueOf(currentItem.getQuantity()));

        // Xử lý sự kiện khi nhấn nút cộng
        holder.plusButton.setOnClickListener(v -> {
            int currentQuantity = currentItem.getQuantity();
            // Cập nhật số lượng trong CartManager
            cartManager.updateItemQuantity(currentItem, currentQuantity + 1);
            // Cập nhật lại giao diện
            notifyItemChanged(position);
            // Thông báo cho Activity biết để cập nhật tổng tiền
            if (cartChangeListener != null) {
                cartChangeListener.onCartChanged();
            }
        });

        // Xử lý sự kiện khi nhấn nút trừ
        holder.minusButton.setOnClickListener(v -> {
            int currentQuantity = currentItem.getQuantity();
            if (currentQuantity > 0) {
                // Cập nhật số lượng trong CartManager, nếu số lượng về 0, item sẽ bị xóa
                cartManager.updateItemQuantity(currentItem, currentQuantity - 1);

                // Kiểm tra xem item có còn trong danh sách không
                if (cartList.contains(currentItem)) {
                    notifyItemChanged(position);
                } else {
                    // Nếu item đã bị xóa, thông báo cho adapter để xóa view
                    notifyDataSetChanged(); // Cập nhật lại toàn bộ list
                }

                // Thông báo cho Activity biết để cập nhật tổng tiền
                if (cartChangeListener != null) {
                    cartChangeListener.onCartChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    // Lớp ViewHolder để giữ các view của mỗi item
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemSize, itemPrice, itemQuantity;
        ImageView plusButton, minusButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.imgFood);
            itemName = itemView.findViewById(R.id.tvName);
            itemSize = itemView.findViewById(R.id.tvSize); // Cần có ID này trong XML
            itemPrice = itemView.findViewById(R.id.tvPrice);
            itemQuantity = itemView.findViewById(R.id.tvQuantity);
            plusButton = itemView.findViewById(R.id.btnPlus);
            minusButton = itemView.findViewById(R.id.btnMinus);
        }
    }
}
