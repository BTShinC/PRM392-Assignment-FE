package com.example.prm392_assignment_food.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_assignment_food.R;
import java.util.List;

public class RunningOrderAdapter extends RecyclerView.Adapter<RunningOrderAdapter.ViewHolder> {

    private List<RunningOrder> runningOrderList;

    public RunningOrderAdapter(List<RunningOrder> runningOrderList) {
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
        holder.tvCategory.setText(order.getCategory());
        holder.tvItemName.setText(order.getName());
        holder.tvItemId.setText(order.getId());
        holder.tvPrice.setText(order.getPrice());
    }

    @Override
    public int getItemCount() {
        return runningOrderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvItemName;
        TextView tvItemId;
        TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemId = itemView.findViewById(R.id.tv_item_id);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
