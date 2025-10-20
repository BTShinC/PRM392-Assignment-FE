package com.example.prm392_assignment_food.data.repository;

import android.util.Log;

import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FoodRepository {
    private static final String TAG = "FoodRepository";
    
    private final ApiService apiService;

    public FoodRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        Log.d(TAG, "FoodRepository initialized");
    }

    /**
     * GET danh sách menu items với pagination
     */
    public void getMenuItems(Integer page, Integer size, String sort,
                            String search, String categoryId,
                            final RepositoryCallback<PageResponse<MenuItemResponse>> callback) {
        Log.d(TAG, "Fetching menu items - page: " + page + ", categoryId: " + categoryId);
        
        Call<PageResponse<MenuItemResponse>> call = apiService.getMenuItems(
                page, size, sort, search, categoryId
        );
        
        call.enqueue(new Callback<PageResponse<MenuItemResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuItemResponse>> call,
                                 Response<PageResponse<MenuItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<MenuItemResponse> pageResponse = response.body();
                    Log.d(TAG, "Success: " + pageResponse.getContent().size() + " items loaded");
                    Log.d(TAG, "Total elements: " + pageResponse.getTotalElements());
                    callback.onSuccess(pageResponse);
                } else {
                    String errorMsg = "Failed - Code: " + response.code();
                    Log.e(TAG, errorMsg);
                    Log.e(TAG, "Response body: " + response.errorBody());
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuItemResponse>> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    /**
     * GET danh sách categories
     */
    public void getMenuCategories(Integer page, Integer size, String sort, String search,
                                 final RepositoryCallback<PageResponse<MenuCategoryResponse>> callback) {
        Log.d(TAG, "Fetching categories");
        
        Call<PageResponse<MenuCategoryResponse>> call = apiService.getMenuCategories(
                page, size, sort, search
        );
        
        call.enqueue(new Callback<PageResponse<MenuCategoryResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuCategoryResponse>> call,
                                 Response<PageResponse<MenuCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Success: Categories loaded");
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed - Code: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuCategoryResponse>> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    /**
     * GET chi tiết menu item theo ID
     */
    public void getMenuItemById(String id, final RepositoryCallback<MenuItemResponse> callback) {
        Log.d(TAG, "Fetching menu item by ID: " + id);
        
        Call<MenuItemResponse> call = apiService.getMenuItemById(id);
        
        call.enqueue(new Callback<MenuItemResponse>() {
            @Override
            public void onResponse(Call<MenuItemResponse> call, Response<MenuItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Success: Menu item loaded");
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed - Code: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<MenuItemResponse> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    /**
     * Callback interface
     */
    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
}

