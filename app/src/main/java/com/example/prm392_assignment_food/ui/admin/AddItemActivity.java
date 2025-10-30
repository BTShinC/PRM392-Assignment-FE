package com.example.prm392_assignment_food.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_assignment_food.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etItemName, etPrice, etDetails;
    private ImageView ivMainImage;
    private Button btnSaveChanges;
    private LinearLayout btnAddPhoto1;

    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        etItemName = findViewById(R.id.et_item_name);
        etPrice = findViewById(R.id.et_price);
        etDetails = findViewById(R.id.et_details);
        ivMainImage = findViewById(R.id.iv_main_image);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnAddPhoto1 = findViewById(R.id.btn_add_photo_1);

        btnAddPhoto1.setOnClickListener(v -> openFileChooser());
        btnSaveChanges.setOnClickListener(v -> uploadImageAndSaveData());
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
            ivMainImage.setImageURI(imageUri);
        }
    }

    private void uploadImageAndSaveData() {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("product_images/" + UUID.randomUUID().toString());

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveDataToFirestore(uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddItemActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataToFirestore(String imageUrl) {
        String itemName = etItemName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String details = etDetails.getText().toString().trim();

        if (itemName.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> product = new HashMap<>();
        product.put("name", itemName);
        product.put("price", Double.parseDouble(price));
        product.put("details", details);
        product.put("imageUrl", imageUrl);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddItemActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddItemActivity.this, "Error adding product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
