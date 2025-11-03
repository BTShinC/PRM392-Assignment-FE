package com.example.prm392_assignment_food.ui.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_assignment_food.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RunningOrderAdapter extends RecyclerView.Adapter<RunningOrderAdapter.ViewHolder> {

    private List<RunningOrder> runningOrderList;
    private OnItemClickListener listener;
    private Context context;
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    public interface OnItemClickListener {
        void onDoneClick(RunningOrder order);
        void onCompleteClick(RunningOrder order);
        void onCancelClick(RunningOrder order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RunningOrderAdapter(Context context, List<RunningOrder> runningOrderList) {
        this.context = context;
        this.runningOrderList = runningOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_running_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RunningOrder order = runningOrderList.get(position);
        holder.tvCategory.setText("#" + order.getCategory());
        holder.tvItemName.setText(order.getName());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(currencyFormatter.format(order.getPrice()));

        String imageUrl = order.getImageUrl();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = BASE_URL + "uploads/" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.chicken)
                .error(R.drawable.background_red_button)
                .into(holder.ivFoodImage);

        String status = order.getStatus();

        // Reset visibility and background
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        holder.btnDone.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnComplete.setVisibility(View.GONE);

        // Set visibility based on status
        if ("PAID".equals(status)) {
            holder.btnDone.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else if ("CONFIRMED".equals(status)) {
            holder.btnDone.setVisibility(View.VISIBLE);
        } else if ("SHIPPING".equals(status)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E0E0E0")); // Darker grey for shipping
            holder.btnComplete.setVisibility(View.VISIBLE);
        }


        holder.btnDone.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoneClick(order);
            }
        });

        holder.btnComplete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCompleteClick(order);
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return runningOrderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvCategory;
        TextView tvItemName;
        TextView tvPrice;
        MaterialButton btnDone;
        MaterialButton btnComplete;
        MaterialButton btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnDone = itemView.findViewById(R.id.btn_done);
            btnComplete = itemView.findViewById(R.id.btn_complete);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}