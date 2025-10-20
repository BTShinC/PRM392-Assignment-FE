package com.example.prm392_assignment_food.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.CartItemResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemResponse> items;
    private final OnItemInteractionListener listener;

    // --- Chế độ chỉnh sửa ---
    private boolean isEditMode = false;
    private Set<CartItemResponse> selectedItems = new HashSet<>();

    public interface OnItemInteractionListener {
        void onIncreaseQuantity(CartItemResponse item);
        void onDecreaseQuantity(CartItemResponse item);
        void onRemoveItem(CartItemResponse item);
        void onSelectionChanged(int selectedSize);
    }

    public CartAdapter(List<CartItemResponse> items, OnItemInteractionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemResponse currentItem = items.get(position);

        holder.itemName.setText(currentItem.getMenuItemName());
        if (currentItem.getUnitPrice() != null) {
            holder.itemPrice.setText(String.format(Locale.US, "$%.2f", currentItem.getUnitPrice()));
        }
        holder.itemQuantity.setText(String.valueOf(currentItem.getQuantity()));
        holder.itemImage.setImageResource(R.drawable.halim);

        // Xử lý hiển thị dựa trên chế độ chỉnh sửa
        if (isEditMode) {
            holder.quantitySelector.setVisibility(View.GONE);
            holder.deleteCheckbox.setVisibility(View.VISIBLE);
            holder.deleteCheckbox.setChecked(selectedItems.contains(currentItem));
        } else {
            holder.quantitySelector.setVisibility(View.VISIBLE);
            holder.deleteCheckbox.setVisibility(View.GONE);
        }

        // Listener cho nút cộng/trừ (chỉ hoạt động khi không ở chế độ chỉnh sửa)
        holder.plusButton.setOnClickListener(v -> {
            if (!isEditMode && listener != null) {
                listener.onIncreaseQuantity(currentItem);
            }
        });

        holder.minusButton.setOnClickListener(v -> {
            if (!isEditMode && listener != null) {
                if (currentItem.getQuantity() <= 1) {
                    listener.onRemoveItem(currentItem);
                } else {
                    listener.onDecreaseQuantity(currentItem);
                }
            }
        });

        // Listener cho CheckBox (chỉ hoạt động khi ở chế độ chỉnh sửa)
        holder.deleteCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(currentItem);
            } else {
                selectedItems.remove(currentItem);
            }
            if (listener != null) {
                listener.onSelectionChanged(selectedItems.size());
            }
        });

        // Listener cho toàn bộ item view để có thể chọn/bỏ chọn dễ dàng hơn
        holder.itemView.setOnClickListener(v -> {
            if (isEditMode) {
                holder.deleteCheckbox.setChecked(!holder.deleteCheckbox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<CartItemResponse> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    // --- Các phương thức cho chế độ chỉnh sửa ---

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        if (!editMode) {
            selectedItems.clear(); // Xóa lựa chọn khi thoát chế độ edit
        }
        notifyDataSetChanged();
    }

    public Set<CartItemResponse> getSelectedItems() {
        return selectedItems;
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice, itemQuantity;
        ImageButton plusButton, minusButton;
        CheckBox deleteCheckbox;
        LinearLayout quantitySelector;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.imgFood);
            itemName = itemView.findViewById(R.id.tvName);
            itemPrice = itemView.findViewById(R.id.tvPrice);
            itemQuantity = itemView.findViewById(R.id.tvQuantity);
            plusButton = itemView.findViewById(R.id.btnPlus);
            minusButton = itemView.findViewById(R.id.btnMinus);
            deleteCheckbox = itemView.findViewById(R.id.checkbox_delete);
            quantitySelector = itemView.findViewById(R.id.quantity_selector);
        }
    }
}
