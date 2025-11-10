package com.example.prm392_assignment_food.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_assignment_food.R;

import java.util.List;

public class RunningOrderAdapter extends RecyclerView.Adapter<RunningOrderAdapter.ViewHolder> {

    private final Context context;
    private final List<RunningOrder> orderList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDoneClick(RunningOrder order);
        void onCompleteClick(RunningOrder order);
        void onCancelClick(RunningOrder order);
        void onItemClick(RunningOrder order);
    }

    public RunningOrderAdapter(Context context, List<RunningOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_running_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RunningOrder order = orderList.get(position);

        holder.tvCategory.setText(order.getCategory());
        holder.tvItemName.setText(order.getName());
        holder.tvPrice.setText(String.format("%,.0f đ", order.getPrice()));

        Glide.with(context).load(order.getImageUrl()).into(holder.ivFoodImage);

        if (listener != null) {
            holder.btnDone.setOnClickListener(v -> listener.onDoneClick(order));
            holder.btnComplete.setOnClickListener(v -> listener.onCompleteClick(order));
            holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(order));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(order));
        }

        // Show/Hide buttons based on the order status
        switch (order.getStatus()) {
            case "PAID":
            case "AWAITING_PAYMENT":
                holder.btnDone.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnComplete.setVisibility(View.GONE);
                break;
            case "CONFIRMED":
                holder.btnDone.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnComplete.setVisibility(View.GONE);
                break;
            case "SHIPPING":
                holder.btnDone.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            default:
                holder.btnDone.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvItemName, tvPrice;
        ImageView ivFoodImage;
        Button btnDone, btnComplete, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            btnDone = itemView.findViewById(R.id.btn_done);
            btnComplete = itemView.findViewById(R.id.btn_complete);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
