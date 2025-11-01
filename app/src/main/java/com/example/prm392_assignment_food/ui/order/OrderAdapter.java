package com.example.prm392_assignment_food.ui.order;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.order.OrderDto;
import com.example.prm392_assignment_food.data.model.order.OrderItemDto;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderInteractionListener {
        void onCancelOrder(String orderId);
    }

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private final List<OrderDto> orderList;
    private final Context context;
    private final ApiService apiService;
    private final OrderInteractionListener listener;

    public OrderAdapter(Context context, List<OrderDto> orderList, OrderInteractionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.apiService = ApiClient.getApiService();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_list, parent, false);
            return new EmptyViewHolder(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OrderViewHolder) {
            OrderViewHolder orderHolder = (OrderViewHolder) holder;
            OrderDto order = orderList.get(position);

            orderHolder.tvOrderCode.setText("Order #" + order.getOrderId().toString().substring(0, 8));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(order.getCreatedAt());
                orderHolder.tvOrderDate.setText(zonedDateTime.format(formatter));
            } catch (Exception e) {
                orderHolder.tvOrderDate.setText(order.getCreatedAt());
            }

            orderHolder.tvTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", order.getTotalPrice()));

            // Trong file OrderAdapter.java, phương thức onBindViewHolder            orderHolder.tvTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", order.getTotalPrice()));

            // THÊM DÒNG NÀY ĐỂ DEBUG
            android.util.Log.d("OrderAdapterStatus", "ID: " + order.getOrderId().toString().substring(0,4) + ", Trạng thái: '" + order.getStatus() + "'");

            Button btnCancel = orderHolder.itemView.findViewById(R.id.btn_cancel_order);

            if ("AWAITING_PAYMENT".equals(order.getStatus().name())) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }

            btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Xác nhận hủy đơn")
                            // --- SỬA LỖI TẠI ĐÂY ---
                            .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + order.getOrderId().toString().substring(0, 8) + "?")
                            // ----------------------
                            .setPositiveButton("Hủy đơn", (dialog, which) -> listener.onCancelOrder(order.getOrderId().toString()))
                            .setNegativeButton("Không", null)
                            .show();
                }
            });

            orderHolder.rvOrderItems.setLayoutManager(new LinearLayoutManager(context));
            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(context, order.getOrderItems(), apiService);
            orderHolder.rvOrderItems.setAdapter(orderItemAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.isEmpty() ? 1 : orderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return orderList.isEmpty() ? VIEW_TYPE_EMPTY : VIEW_TYPE_ITEM;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvOrderDate, tvTotalAmount;
        RecyclerView rvOrderItems;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            rvOrderItems = itemView.findViewById(R.id.rv_order_items);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

        private List<OrderItemDto> orderItemList;
        private Context context;
        private ApiService apiService;

        public OrderItemAdapter(Context context, List<OrderItemDto> orderItemList, ApiService apiService) {
            this.context = context;
            this.orderItemList = orderItemList;
            this.apiService = apiService;
        }

        @NonNull
        @Override
        public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_order, parent, false);
            return new OrderItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
            OrderItemDto orderItem = orderItemList.get(position);

            holder.tvQuantity.setText("x" + orderItem.getQuantity());

            apiService.getMenuItemById(String.valueOf(orderItem.getMenuItemId())).enqueue(new Callback<MenuItemResponse>() {
                @Override
                public void onResponse(Call<MenuItemResponse> call, Response<MenuItemResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MenuItemResponse menuItem = response.body();
                        holder.tvName.setText(menuItem.getName());
                        holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", menuItem.getPrice()));
                        Glide.with(context).load(menuItem.getImageUrl()).into(holder.imgFood);
                    } else {
                        Toast.makeText(context, "Failed to load menu item details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MenuItemResponse> call, Throwable t) {
                    Toast.makeText(context, "Error loading item: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderItemList != null ? orderItemList.size() : 0;
        }

        public class OrderItemViewHolder extends RecyclerView.ViewHolder {
            ImageView imgFood;
            TextView tvName, tvQuantity, tvPrice;

            public OrderItemViewHolder(@NonNull View itemView) {
                super(itemView);
                imgFood = itemView.findViewById(R.id.imgFood);
                tvName = itemView.findViewById(R.id.tvName);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvPrice = itemView.findViewById(R.id.tvPrice);
            }
        }
    }
}
