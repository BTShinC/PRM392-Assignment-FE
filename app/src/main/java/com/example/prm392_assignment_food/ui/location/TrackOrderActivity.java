package com.example.prm392_assignment_food.ui.location;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackOrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private TextView tvRestaurantName, tvRestaurantAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        mapView = findViewById(R.id.mapView);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Dữ liệu có thể được đặt ở đây từ Intent hoặc API call
        // Ví dụ:
        // String restaurantName = getIntent().getStringExtra("RESTAURANT_NAME");
        // tvRestaurantName.setText(restaurantName);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Convert 150dp (peekHeight) to pixels for bottom padding
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int padding = (int) (150 * displayMetrics.density);
        googleMap.setPadding(0, 0, 0, padding);

        // --- GHIM VỊ TRÍ TẠI TP. HỒ CHÍ MINH ---
        // Tọa độ của Quán Yummy
        LatLng yummyQuanLocation = new LatLng(10.79047772693391, 106.71594203047574);

        // Thêm ghim (marker) vào bản đồ
        googleMap.addMarker(new MarkerOptions()
                .position(yummyQuanLocation)
                .title("Quán Yummy"));

        // Di chuyển camera đến vị trí đã ghim và zoom vào
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yummyQuanLocation, 16));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
