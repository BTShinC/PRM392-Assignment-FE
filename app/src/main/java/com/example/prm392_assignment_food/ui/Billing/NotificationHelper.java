package com.example.prm392_assignment_food.ui.Billing;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log; // <<< THÊM LOG

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.prm392_assignment_food.R;
// ⚠️ THAY THẾ DÒNG NÀY bằng Activity bạn muốn mở khi người dùng bấm vào
import com.example.prm392_assignment_food.ui.chat.InboxActivity;
import com.example.prm392_assignment_food.ui.chat.NotificationType;
import java.util.Random;

public class NotificationHelper {

    public static final String CHANNEL_ID = "YUMMY_GO_CHANNEL";
    private static final String CHANNEL_NAME = "YummyGo Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for order and payment status";

    // ✅ Đã sửa: Luôn gọi hàm này trước
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Sử dụng IMPORTANCE_HIGH để thông báo nổi lên
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);

            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("NotificationHelper", "✅ Kênh thông báo đã được tạo!");
            } else {
                Log.e("NotificationHelper", "💥 Lỗi: NotificationManager là null!");
            }
        }
    }

    public static void showNotification(Context context, NotificationType type, String title, String message) {

        // <<< SỬA LỖI QUAN TRỌNG NHẤT >>>
        // Luôn gọi hàm tạo kênh trước khi hiển thị bất cứ thứ gì.
        createNotificationChannel(context);


        // <<< THÊM MỚI >>> Tạo Intent
        Intent intent = new Intent(context, InboxActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        // <<< SỬA LỖI THỨ 2: DÙNG ICON HỆ THỐNG ĐỂ KIỂM TRA >>>
        // Chúng ta tạm thời dùng icon "i" (info) của Android
        // để chắc chắn 100% là code không lỗi do thiếu icon
        int iconRes = android.R.drawable.ic_dialog_info;

        // Bạn có thể mở lại khối switch này SAU KHI đã thấy thông báo hoạt động
        /*
        int colorRes = R.color.notification_general;
        if (type != null) {
            switch (type) {
                // ... (khối switch của bạn) ...
            }
        }
        */

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconRes) // ✅ Sửa: Dùng icon hệ thống an toàn
                .setContentTitle(title)
                .setContentText(message)
                // .setColor(ContextCompat.getColor(context, colorRes)) // Tạm thời tắt
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Ưu tiên cao
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Thêm Intent

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("NotificationHelper", "💥 LỖI: Đã gọi showNotification nhưng KHÔNG CÓ QUYỀN!");
            return;
        }

        notificationManager.notify(new Random().nextInt(), builder.build());
        Log.d("NotificationHelper", "✅ ĐÃ GỌI NOTIFY. Kiểm tra điện thoại!");
    }
}