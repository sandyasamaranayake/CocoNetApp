package com.s23010692.coconetapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddProductsActivity extends AppCompatActivity {

    ImageView ivProductImage;
    EditText etName, etQuantity, etPrice, etLocation;
    Button btnSelectImage, btnSubmitProduct;
    Uri selectedImageUri;
    DBHelper dbHelper;
    String loggedInEmail;
    FusedLocationProviderClient fusedLocationClient;
    double latitude = 0.0, longitude = 0.0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_add_products);

        ivProductImage = findViewById(R.id.ivProductImage);
        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmitProduct = findViewById(R.id.btnSubmitProduct);
        dbHelper = new DBHelper(this);

        // Get logged-in email from intent
        loggedInEmail = getIntent().getStringExtra("userEmail");
        if (loggedInEmail == null) loggedInEmail = "";

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permission if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        ActivityResultLauncher<Intent> imagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            ivProductImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSubmitProduct.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String location = etLocation.getText().toString().trim();

            if (selectedImageUri == null || name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please select image and fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            double price;
            try {
                quantity = Integer.parseInt(qtyStr);
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid numbers for quantity and price", Toast.LENGTH_SHORT).show();
                return;
            }

            String imageFileName = "product_" + System.currentTimeMillis() + ".jpg";
            File destFile = new File(getFilesDir(), imageFileName);

            try (InputStream in = getContentResolver().openInputStream(selectedImageUri);
                 OutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                return;
            }

            String storedImageUri = Uri.fromFile(destFile).toString();

            // Try to get location, but allow adding product even if it fails
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, proceed with default coordinates
                boolean success = dbHelper.insertProduct(name, quantity, location, price, storedImageUri, loggedInEmail, 0.0, 0.0);
                if (success) {
                    Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MyProductsActivity.class);
                    intent.putExtra("userEmail", loggedInEmail);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add product.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(locationObj -> {
                        double lat = 0.0, lng = 0.0;
                        if (locationObj != null) {
                            lat = locationObj.getLatitude();
                            lng = locationObj.getLongitude();
                        }
                        boolean success = dbHelper.insertProduct(name, quantity, location, price, storedImageUri, loggedInEmail, lat, lng);
                        if (success) {
                            Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MyProductsActivity.class);
                            intent.putExtra("userEmail", loggedInEmail);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to add product.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Location failed, proceed with default coordinates
                        boolean success = dbHelper.insertProduct(name, quantity, location, price, storedImageUri, loggedInEmail, 0.0, 0.0);
                        if (success) {
                            Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MyProductsActivity.class);
                            intent.putExtra("userEmail", loggedInEmail);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to add product.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
