package com.example.prm392_assignment_food.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ApiResponseDto;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.OrderResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.ui.Billing.OrderItem;
import com.example.prm392_assignment_food.ui.Billing.OrderItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderDetailDialogFragment extends DialogFragment {

    public static final String TAG = "AdminOrderDetailDialog";
    private static final String ARG_ORDER_ID = "order_id";

    private TextView tvCustomerName, tvCustomerAddress, tvCustomerPhone, tvTotalPrice;
    private RecyclerView rvOrderItems;
    private ApiService apiService;

    public static AdminOrderDetailDialogFragment newInstance(String orderId) {
        AdminOrderDetailDialogFragment fragment = new AdminOrderDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_admin_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCustomerName = view.findViewById(R.id.tv_customer_name);
        tvCustomerAddress = view.findViewById(R.id.tv_customer_address);
        tvCustomerPhone = view.findViewById(R.id.tv_customer_phone);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        rvOrderItems = view.findViewById(R.id.rv_order_items);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(getContext()));
        
        Button btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dismiss());

        apiService = ApiClient.getApiService();

        if (getArguments() != null) {
            String orderId = getArguments().getString(ARG_ORDER_ID);
            if (orderId != null) {
                fetchOrderDetails(orderId);
            }
        }
    }

    private void fetchOrderDetails(String orderId) {
        apiService.getOrderDetail(orderId).enqueue(new Callback<ApiResponseDto<OrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<OrderResponse>> call, Response<ApiResponseDto<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    OrderResponse order = response.body().getData();
                    if (order.users != null) {
                        tvCustomerName.setText("Tên: " + order.users.name);
                        tvCustomerAddress.setText("Địa chỉ: " + order.users.address);
                        tvCustomerPhone.setText("SĐT: " + order.users.phone);
                    } else {
                        Toast.makeText(getContext(), "User details are not available", Toast.LENGTH_SHORT).show();
                    }
                    tvTotalPrice.setText(String.format("Tổng: %,.0f đ", order.totalPrice));

                    // Fetch all menu items to get their names and images
                    apiService.getMenuItems(0, 1000, "name,asc", null, null).enqueue(new Callback<PageResponse<MenuItemResponse>>() {
                        @Override
                        public void onResponse(Call<PageResponse<MenuItemResponse>> call, Response<PageResponse<MenuItemResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, MenuItemResponse> menuItemMap = new HashMap<>();
                                for (MenuItemResponse item : response.body().getContent()) {
                                    menuItemMap.put(item.getId(), item);
                                }

                                List<OrderItem> billingOrderItems = new ArrayList<>();
                                for (OrderResponse.OrderItemResponse responseItem : order.orderItems) {
                                    MenuItemResponse menuItemDetails = menuItemMap.get(responseItem.menuItemId);
                                    if (menuItemDetails != null) {
                                        billingOrderItems.add(new OrderItem(menuItemDetails.getImageUrl(), menuItemDetails.getName(), (double) responseItem.price, "Vừa", responseItem.quantity));
                                    }
                                }
                                rvOrderItems.setAdapter(new OrderItemAdapter(billingOrderItems));
                            } else {
                                Toast.makeText(getContext(), "Failed to load menu item details.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PageResponse<MenuItemResponse>> call, Throwable t) {
                            Toast.makeText(getContext(), "Error loading menu item details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<OrderResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
