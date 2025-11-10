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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        } else {
            apiService.getOrders(0, 50, Collections.singletonList("createdAt,DESC"), currentStatus, null).enqueue(new Callback<ApiResponseDto<PageResponse<OrderResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<PageResponse<OrderResponse>>> call, Response<ApiResponseDto<PageResponse<OrderResponse>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        if (response.body().getData().getContent().isEmpty()) {
                            Toast.makeText(getContext(), "No orders found with status: " + currentStatus, Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            fetchAllMenuItemsAndThenProcessOrders(response.body().getData().getContent());
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
                        fetchAllMenuItemsAndThenProcessOrders(combinedOrders);
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
                        fetchAllMenuItemsAndThenProcessOrders(combinedOrders);
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
                        fetchAllMenuItemsAndThenProcessOrders(combinedOrders);
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
                        fetchAllMenuItemsAndThenProcessOrders(combinedOrders);
                    }
                }
            }
        };

        apiService.getOrders(0, 25, Collections.singletonList("createdAt,DESC"), "CONFIRMED", null).enqueue(callback);
        apiService.getOrders(0, 25, Collections.singletonList("createdAt,DESC"), "SHIPPING", null).enqueue(callback);
    }

    private void fetchAllMenuItemsAndThenProcessOrders(List<OrderResponse> orderResponses) {
        apiService.getMenuItems(0, 1000, "name,asc", null, null).enqueue(new Callback<PageResponse<MenuItemResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuItemResponse>> call, Response<PageResponse<MenuItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, MenuItemResponse> menuItemMap = new HashMap<>();
                    for (MenuItemResponse item : response.body().getContent()) {
                        menuItemMap.put(item.getId(), item);
                    }
                    processOrdersWithMenuItemMap(orderResponses, menuItemMap);
                } else {
                    Toast.makeText(getContext(), "Failed to load menu item details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuItemResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading menu item details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processOrdersWithMenuItemMap(List<OrderResponse> orderResponses, Map<String, MenuItemResponse> menuItemMap) {
        Map<String, OrderResponse> uniqueOrders = new LinkedHashMap<>();
        for (OrderResponse order : orderResponses) {
            if (order.users != null && order.users.userId != null && order.orderItems != null && !order.orderItems.isEmpty()) {

                // Create a unique "fingerprint" based on the order's content
                // to definitively eliminate duplicates caused by backend issues.
                List<String> itemFingerprints = new ArrayList<>();
                for (OrderResponse.OrderItemResponse item : order.orderItems) {
                    itemFingerprints.add(item.menuItemId + "x" + item.quantity);
                }
                Collections.sort(itemFingerprints); // Sort to ensure consistency
                String itemsKey = String.join(";", itemFingerprints);

                String uniqueKey = order.users.userId + "|" + order.totalPrice + "|" + itemsKey;
                
                uniqueOrders.put(uniqueKey, order);

            } else if (order.orderId != null) {
                // Fallback for orders with missing data
                uniqueOrders.put(order.orderId, order);
            }
        }

        runningOrderList.clear();
        for (OrderResponse orderResponse : uniqueOrders.values()) {
            if (orderResponse.orderItems != null && !orderResponse.orderItems.isEmpty()) {
                OrderResponse.OrderItemResponse firstItem = orderResponse.orderItems.get(0);
                MenuItemResponse menuItemDetails = menuItemMap.get(firstItem.menuItemId);

                if (menuItemDetails != null) {
                    String displayName;
                    int otherItemsCount = orderResponse.orderItems.size() - 1;

                    if (otherItemsCount > 0) {
                        displayName = menuItemDetails.getName() + " và " + otherItemsCount + " món khác";
                    } else {
                        displayName = menuItemDetails.getName();
                    }

                    RunningOrder runningOrder = new RunningOrder(
                            orderResponse.orderId,
                            orderResponse.users.name,
                            displayName,
                            orderResponse.totalPrice,
                            menuItemDetails.getImageUrl(),
                            orderResponse.orderStatus
                    );
                    runningOrderList.add(runningOrder);
                }
            }
        }
        adapter.notifyDataSetChanged();
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
    
    @Override
    public void onItemClick(RunningOrder order) {
        AdminOrderDetailDialogFragment.newInstance(order.getId()).show(getParentFragmentManager(), AdminOrderDetailDialogFragment.TAG);
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
