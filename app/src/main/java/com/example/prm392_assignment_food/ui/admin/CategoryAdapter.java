package com.example.prm392_assignment_food.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<MenuCategoryResponse> categoryList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onUpdateClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryAdapter(List<MenuCategoryResponse> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_manage, parent, false);
        return new CategoryViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MenuCategoryResponse currentCategory = categoryList.get(position);
        holder.tvCategoryName.setText(currentCategory.getName());
        holder.tvCategoryDescription.setText(currentCategory.getDescription());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategoryName;
        public TextView tvCategoryDescription;
        public ImageView ivUpdate;
        public ImageView ivDelete;

        public CategoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDescription = itemView.findViewById(R.id.tv_category_description);
            ivUpdate = itemView.findViewById(R.id.iv_update_category);
            ivDelete = itemView.findViewById(R.id.iv_delete_category);

            ivUpdate.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUpdateClick(position);
                    }
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
