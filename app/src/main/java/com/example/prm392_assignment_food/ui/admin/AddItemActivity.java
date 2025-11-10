package com.example.prm392_assignment_food.ui.admin;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.bumptech.glide.Glide;
import com.example.prm392_assignment_food.R;
import com.example.prm392_assignment_food.data.model.MenuCategoryResponse;
import com.example.prm392_assignment_food.data.model.MenuItemRequest;
import com.example.prm392_assignment_food.data.model.MenuItemResponse;
import com.example.prm392_assignment_food.data.model.PageResponse;
import com.example.prm392_assignment_food.data.network.ApiClient;
import com.example.prm392_assignment_food.data.network.ApiService;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddItemActivity";

    private EditText etItemName, etPrice, etDescription;
    private ImageView ivMainImage;
    private Button btnSaveItem;
    private LinearLayout btnAddPhoto;
    private Spinner spinnerCategory;
    private SwitchCompat switchIsAvailable;

    private Uri imageUri;
    private ApiService apiService;
    private List<MenuCategoryResponse> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        apiService = ApiClient.getApiService();

        // Initialize views
        etItemName = findViewById(R.id.et_item_name);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        ivMainImage = findViewById(R.id.iv_main_image);
        btnSaveItem = findViewById(R.id.btn_save_item);
        btnAddPhoto = findViewById(R.id.btn_add_photo);
        spinnerCategory = findViewById(R.id.spinner_category);
        switchIsAvailable = findViewById(R.id.switch_is_available);

        // Setup Spinner
        setupCategorySpinner();

        // Set listeners
        btnAddPhoto.setOnClickListener(v -> openFileChooser());
        btnSaveItem.setOnClickListener(v -> saveData());

        // Fetch categories from API
        fetchCategories();
    }

    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void fetchCategories() {
        apiService.getMenuCategories(0, 100, "name,asc", null).enqueue(new Callback<PageResponse<MenuCategoryResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MenuCategoryResponse>> call, Response<PageResponse<MenuCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body().getContent();
                    List<String> categoryNames = new ArrayList<>();
                    for (MenuCategoryResponse category : categoryList) {
                        categoryNames.add(category.getName());
                    }
                    categoryAdapter.clear();
                    categoryAdapter.addAll(categoryNames);
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddItemActivity.this, "Tải danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MenuCategoryResponse>> call, Throwable t) {
                Toast.makeText(AddItemActivity.this, "Lỗi khi tải danh mục: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivMainImage);
        }
    }

    private void saveData() {
        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn một ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate inputs
        String itemName = etItemName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();

        if (itemName.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategoryPosition < 0 || selectedCategoryPosition >= categoryList.size()) {
            Toast.makeText(this, "Vui lòng chọn một danh mục hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get data from views
        String categoryId = categoryList.get(selectedCategoryPosition).getCategoryId();
        double price = Double.parseDouble(priceStr);
        boolean isAvailable = switchIsAvailable.isChecked();

        // Create request object and convert to JSON
        MenuItemRequest menuItemRequest = new MenuItemRequest(categoryId, itemName, description, price, isAvailable);
        Gson gson = new Gson();
        String menuItemRequestJson = gson.toJson(menuItemRequest);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), menuItemRequestJson);

        // Create file part
        File file = getFileFromUri(this, imageUri);
        if (file == null) {
             Toast.makeText(this, "Lấy tệp từ Uri thất bại", Toast.LENGTH_SHORT).show();
             return;
        }
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);


        // Make API call
        apiService.addMenuItem(requestBody, filePart).enqueue(new Callback<MenuItemResponse>() {
            @Override
            public void onResponse(Call<MenuItemResponse> call, Response<MenuItemResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddItemActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish(); // Go back to the previous activity
                } else {
                    String errorMsg = "Thêm sản phẩm thất bại. Mã lỗi: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ", " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi phân tích nội dung lỗi", e);
                    }
                    Toast.makeText(AddItemActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi API: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<MenuItemResponse> call, Throwable t) {
                Log.e(TAG, "API Failure: ", t);
                Toast.makeText(AddItemActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static File getFileFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String fileName = getFileName(context, uri);
        File file = new File(context.getCacheDir(), fileName);
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024]; // 4K buffer
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Sao chép tệp từ Uri thất bại", e);
            return null;
        }
        return file;
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
