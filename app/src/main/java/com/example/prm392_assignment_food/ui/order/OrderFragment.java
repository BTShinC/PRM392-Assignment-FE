package com.example.prm392_assignment_food.ui.order;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ResponseDto;
import com.example.prm392_assignment_food.data.model.order.OrderDto;
import com.example.prm392_assignment_food.data.model.order.OrderItemDto;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.example.prm392_assignment_food.utils.JwtUtils;
import com.example.prm392_assignment_food.utils.TokenManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment implements View.OnClickListener, OrderAdapter.OrderInteractionListener {

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private List<OrderDto> orderList = new ArrayList<>();
    private ApiService apiService;
    private TokenManager tokenManager;
    private String userId;
    private ProgressBar progressBar;

    private TextView tvAwaitingPayment, tvPaid, tvPaymentFailed, tvConfirmed, tvShipping, tvDelivered, tvCompleted, tvCancelled;
    private TextView currentStatusView;
    private String currentStatusString = "";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ApiClient.init(requireActivity());
        apiService = ApiClient.getClient().create(ApiService.class);

        tokenManager = new TokenManager(requireActivity());
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
            return;
        }

        this.userId = JwtUtils.getUserId(token);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "Token không hợp lệ, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar = rootView.findViewById(R.id.progressBar);
        rvOrders = rootView.findViewById(R.id.rv_orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderAdapter = new OrderAdapter(requireContext(), orderList, this);
        rvOrders.setAdapter(orderAdapter);

        initStatusTabs();

        loadOrdersByStatus("AWAITING_PAYMENT", tvAwaitingPayment);
    }

    private void initStatusTabs() {
        tvAwaitingPayment = rootView.findViewById(R.id.AWAITING_PAYMENT);
        tvPaid = rootView.findViewById(R.id.PAID);
        tvPaymentFailed = rootView.findViewById(R.id.PAYMENT_FAILED);
        tvConfirmed = rootView.findViewById(R.id.CONFIRMED);
        tvShipping = rootView.findViewById(R.id.SHIPPING);
        tvDelivered = rootView.findViewById(R.id.DELIVERED);
        tvCompleted = rootView.findViewById(R.id.COMPLETED);
        tvCancelled = rootView.findViewById(R.id.CANCELLED);

        tvAwaitingPayment.setOnClickListener(this);
        tvPaid.setOnClickListener(this);
        tvPaymentFailed.setOnClickListener(this);
        tvConfirmed.setOnClickListener(this);
        tvShipping.setOnClickListener(this);
        tvDelivered.setOnClickListener(this);
        tvCompleted.setOnClickListener(this);
        tvCancelled.setOnClickListener(this);
    }

    private void loadOrdersByStatus(String status, TextView statusView) {
        if (currentStatusView != null) {
            currentStatusView.setBackgroundResource(R.drawable.tab_background_unselected);
            currentStatusView.setTypeface(null, Typeface.NORMAL);
        }
        statusView.setBackgroundResource(R.drawable.tab_background_selected);
        statusView.setTypeface(null, Typeface.BOLD);
        currentStatusView = statusView;
        currentStatusString = status;

        progressBar.setVisibility(View.VISIBLE);
        rvOrders.setVisibility(View.GONE);

        apiService.getOrdersByStatus(userId, status).enqueue(new Callback<ResponseDto<List<OrderDto>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<OrderDto>>> call, Response<ResponseDto<List<OrderDto>>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                rvOrders.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess() && response.body().getData() != null) {
                        processAndDisplayOrders(response.body().getData());
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseDto<List<OrderDto>>> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                rvOrders.setVisibility(View.VISIBLE);
                orderList.clear();
                orderAdapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderFragment", "API call failed", t);
            }
        });
    }

    private void processAndDisplayOrders(List<OrderDto> orders) {
        Map<String, OrderDto> uniqueOrders = new LinkedHashMap<>();
        for (OrderDto order : orders) {
            String fingerprint = createOrderFingerprint(order);
            uniqueOrders.put(fingerprint, order);
        }
        orderList.clear();
        orderList.addAll(uniqueOrders.values());
    }

    private String createOrderFingerprint(OrderDto order) {
        List<String> itemFingerprints = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItemDto item : order.getOrderItems()) {
                itemFingerprints.add(item.getMenuItemId() + "x" + item.getQuantity());
            }
        }
        Collections.sort(itemFingerprints);
        // Use the user ID from the order's user object for the fingerprint
        String userIdString = (order.getUsers() != null && order.getUsers().getUserId() != null) ? order.getUsers().getUserId().toString() : "unknown";
        return userIdString + "|" + order.getTotalPrice() + "|" + String.join(";", itemFingerprints);
    }


    @Override
    public void onCancelOrder(String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.updateOrderStatus(orderId, "CANCELLED").enqueue(new Callback<ResponseDto>() {
            @Override
            public void onResponse(Call<ResponseDto> call, Response<ResponseDto> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(requireContext(), "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                    // Simply reload the list for the current status. The fingerprinting will handle removal.
                    loadOrdersByStatus(currentStatusString, currentStatusView);
                } else {
                    String errorMsg = "Có lỗi xảy ra, vui lòng thử lại.";
                    if(response.body() != null && response.body().getMessage() != null){
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDto> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.AWAITING_PAYMENT) {
            loadOrdersByStatus("AWAITING_PAYMENT", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.PAID) {
            loadOrdersByStatus("PAID", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.PAYMENT_FAILED) {
            loadOrdersByStatus("PAYMENT_FAILED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.CONFIRMED) {
            loadOrdersByStatus("CONFIRMED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.SHIPPING) {
            loadOrdersByStatus("SHIPPING", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.DELIVERED) {
            loadOrdersByStatus("DELIVERED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.COMPLETED) {
            loadOrdersByStatus("COMPLETED", (TextView) v);
            scrollTabToCenter((TextView) v);
        } else if (id == R.id.CANCELLED) {
            loadOrdersByStatus("CANCELLED", (TextView) v);
            scrollTabToCenter((TextView) v);
        }
    }

    private void scrollTabToCenter(TextView selectedTab) {
        HorizontalScrollView scrollView = rootView.findViewById(R.id.tab_scroll);
        int scrollX = selectedTab.getLeft() + selectedTab.getWidth() / 2 - scrollView.getWidth() / 2;
        scrollView.smoothScrollTo(scrollX, 0);
    }
}
