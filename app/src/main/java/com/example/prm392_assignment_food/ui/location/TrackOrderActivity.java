package com.example.prm392_assignment_food.ui.location;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Convert 250dp to pixels (200dp for bottom sheet + 50dp margin)
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int padding = (int) (250 * displayMetrics.density);
        googleMap.setPadding(0, 0, 0, padding);

        // Add a marker for the restaurant and customer locations
        LatLng restaurantLocation = new LatLng(23.8103, 90.4125); // Example location
        LatLng customerLocation = new LatLng(23.8150, 90.4225); // Example location

        googleMap.addMarker(new MarkerOptions().position(restaurantLocation).title("Uttora Coffee House"));
        googleMap.addMarker(new MarkerOptions().position(customerLocation).title("Your Location"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, 12));
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
