package com.example.prm392_assignment_food.data.network;

import android.content.Context;
import androidx.annotation.NonNull;
import com.example.prm392_assignment_food.utils.TokenManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    // Constructor để nhận Context, dùng để khởi tạo TokenManager
    public AuthInterceptor(Context context) {
        // Dùng getApplicationContext() để tránh memory leak
        this.tokenManager = new TokenManager(context.getApplicationContext());
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // Lấy request gốc
        Request originalRequest = chain.request();

        // Lấy token từ SharedPreferences
        String token = tokenManager.getToken();

        // Nếu có token, thêm header Authorization vào request
        if (token != null && !token.isEmpty()) {
            Request.Builder builder = originalRequest.newBuilder()
                    // >>> ĐÂY LÀ DÒNG QUAN TRỌNG NHẤT <<<
                    // Thêm header với định dạng "Bearer <token>"
                    .header("Authorization", "Bearer " + token);

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }

        // Nếu không có token, cho request đi tiếp mà không thay đổi
        return chain.proceed(originalRequest);
    }
}
