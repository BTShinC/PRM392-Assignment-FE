package com.example.prm392_assignment_food.ui.admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.ApiResponseDto;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.OrderResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RunningOrdersBottomSheetFragment extends BottomSheetDialogFragment implements RunningOrderAdapter.OnItemClickListener {

    public static final String TAG = "RunningOrdersBottomSheetFragment";
    private static final String ARG_STATUS = "status";
    public static final String REQUEST_KEY = "requestRefresh";


    private ApiService apiService;
    private List<RunningOrder> runningOrderList = new ArrayList<>();
    private RunningOrderAdapter adapter;
    private String currentStatus;

    public static RunningOrdersBottomSheetFragment newInstance(String status) {
        RunningOrdersBottomSheetFragment fragment = new RunningOrdersBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, new Bundle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentStatus = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_running_orders, container, false);

        apiService = ApiClient.getApiService();

        RecyclerView rvRunningOrders = view.findViewById(R.id.rv_running_orders);
        rvRunningOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RunningOrderAdapter(requireContext(), runningOrderList);
        adapter.setOnItemClickListener(this);
        rvRunningOrders.setAdapter(adapter);

        fetchOrdersByStatus();

        return view;
    }

    private void fetchOrdersByStatus() {
        if (currentStatus == null) return;

        if ("CONFIRMED".equals(currentStatus)) {
            fetchConfirmedAndShippingOrders();
        } else if ("PAID".equals(currentStatus)) {
            fetchPaidAndAwaitingPaymentOrders();
        }
        else {
            apiService.getOrders(0, 50, Collections.singletonList("createdAt,DESC"), currentStatus, null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        if (response.body().getData().getContent().isEmpty()) {
                            Toast.makeText(getContext(), "No orders found with status: " + currentStatus, Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            processOrders(response.body().getData().getContent());
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchPaidAndAwaitingPaymentOrders() {
        final List<OrderResponse> combinedOrders = new ArrayList<>();
        final AtomicInteger callCounter = new AtomicInteger(2);

        Callback<ApiResponseDto<PageResponse<OrderResponse>>> callback = new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    combinedOrders.addAll(response.body().getData().getContent());
                }
                if (callCounter.decrementAndGet() == 0) {
                    if (combinedOrders.isEmpty()) {
                        Toast.makeText(getContext(), "No order requests found", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        processOrders(combinedOrders);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                if (callCounter.decrementAndGet() == 0) {
                     if (combinedOrders.isEmpty()) {
                        Toast.makeText(getContext(), "Error fetching some orders", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        processOrders(combinedOrders);
                    }
                }
            }
        };

        apiService.getOrders(0, 25, Collections.singletonList("createdAt,DESC"), "PAID", null).enqueue(callback);
        apiService.getOrders(0, 25, Collections.singletonList("createdAt,DESC"), "AWAITING_PAYMENT", null).enqueue(callback);
    }


    private void fetchConfirmedAndShippingOrders() {
        final List<OrderResponse> combinedOrders = new ArrayList<>();
        final AtomicInteger callCounter = new AtomicInteger(2);

        Callback<ApiResponseDto<PageResponse<OrderResponse>>> callback = new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    combinedOrders.addAll(response.body().getData().getContent());
                }
                if (callCounter.decrementAndGet() == 0) {
                    if (combinedOrders.isEmpty()) {
                        Toast.makeText(getContext(), "No running orders found", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        processOrders(combinedOrders);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Throwable t) {
                if (callCounter.decrementAndGet() == 0) {
                    if (combinedOrders.isEmpty()) {
                        Toast.makeText(getContext(), "Error fetching some orders", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        processOrders(combinedOrders);
                    }
                }
            }
        };

        apiService.getOrders(0, 25, Collections.singletonList("createdAt,DESC"), "CONFIRMED", null).enqueue(callback);
        apiService.getOrders(0, 25, Collections.singletonList("createdAt,DESC"), "SHIPPING", null).enqueue(callback);
    }


    private void processOrders(List<OrderResponse> orderResponses) {
        runningOrderList.clear();
        AtomicInteger counter = new AtomicInteger(orderResponses.size());

        for (OrderResponse orderResponse : orderResponses) {
            if (orderResponse.orderItems != null && !orderResponse.orderItems.isEmpty()) {
                String menuItemId = orderResponse.orderItems.get(0).menuItemId;
                apiService.getMenuItemById(menuItemId).enqueue(new Callback<MenuItemResponse>() {
                    @Override
                    public void onResponse(Call<MenuItemResponse> call, Response<MenuItemResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MenuItemResponse menuItem = response.body();
                            runningOrderList.add(new RunningOrder(
                                    orderResponse.orderId,
                                    menuItem.getCategoryName(),
                                    menuItem.getName(),
                                    orderResponse.totalPrice,
                                    menuItem.getImageUrl(),
                                    orderResponse.orderStatus
                            ));
                        }
                        if (counter.decrementAndGet() == 0) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<MenuItemResponse> call, Throwable t) {
                        if (counter.decrementAndGet() == 0) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                if (counter.decrementAndGet() == 0) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onDoneClick(RunningOrder order) {
        String nextStatus = "";
        if ("PAID".equals(order.getStatus()) || "AWAITING_PAYMENT".equals(order.getStatus())) {
            nextStatus = "CONFIRMED";
        } else if ("CONFIRMED".equals(order.getStatus())) {
            nextStatus = "SHIPPING";
        }
        updateStatus(order, nextStatus);
    }

    @Override
    public void onCompleteClick(RunningOrder order) {
        updateStatus(order, "COMPLETED");
    }

    @Override
    public void onCancelClick(RunningOrder order) {
        updateStatus(order, "CANCELLED");
    }

    private void updateStatus(RunningOrder order, String newStatus) {
        if (newStatus.isEmpty()) return;


        apiService.adminUpdateOrderStatus(order.getId(), newStatus).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Order status updated to " + newStatus, Toast.LENGTH_SHORT).show();

                    int position = -1;
                    for (int i = 0; i < runningOrderList.size(); i++) {
                        if (runningOrderList.get(i).getId().equals(order.getId())) {
                            position = i;
                            break;
                        }
                    }

                    if (position == -1) return;

                    if ("COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus)) {
                        runningOrderList.remove(position);
                        adapter.notifyItemRemoved(position);
                    } else {
                        runningOrderList.get(position).setStatus(newStatus);
                        adapter.notifyItemChanged(position);
                    }
                    
                    if (runningOrderList.isEmpty()) {
                        dismiss();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to update order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
