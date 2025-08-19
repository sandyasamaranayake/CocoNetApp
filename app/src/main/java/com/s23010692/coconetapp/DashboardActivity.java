package com.s23010692.coconetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnMyProducts, btnAddProduct, btnSalesReport;
    Button btnMarketplace, btnMyOrders, btnMap;
    Button btnFeedback;

    String userEmail = "userEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnMyProducts = findViewById(R.id.btnMyProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnSalesReport = findViewById(R.id.btnSalesReport);
        btnMarketplace = findViewById(R.id.btnMarketplace);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        btnMap = findViewById(R.id.btnMap);
        btnFeedback = findViewById(R.id.btnFeedback);


        String userRole = getIntent().getStringExtra("userRole");
        userEmail = getIntent().getStringExtra("userEmail");
        if (userRole == null) userRole = "user";

        tvWelcome.setText("Welcome, " + userRole);


        if (userRole.equalsIgnoreCase("farmer")) {
            btnMyProducts.setVisibility(View.VISIBLE);
            btnAddProduct.setVisibility(View.VISIBLE);
            btnSalesReport.setVisibility(View.VISIBLE);

            btnMarketplace.setVisibility(View.GONE);
            btnMyOrders.setVisibility(View.GONE);
            btnMap.setVisibility(View.GONE);
            btnFeedback.setVisibility(View.GONE);
        } else if (userRole.equalsIgnoreCase("buyer")) {
            btnMarketplace.setVisibility(View.VISIBLE);
            btnMyOrders.setVisibility(View.VISIBLE);
            btnMap.setVisibility(View.VISIBLE);
            btnFeedback.setVisibility(View.VISIBLE);

            btnMyProducts.setVisibility(View.GONE);
            btnAddProduct.setVisibility(View.GONE);
            btnSalesReport.setVisibility(View.GONE);
        }


        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProductsActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        btnMyProducts.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyProductsActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        btnSalesReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, SalesReportActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        btnMarketplace.setOnClickListener(v -> {
            Intent intent = new Intent(this, MarketplaceActivity.class);
            intent.putExtra("buyerEmail", userEmail);
            startActivity(intent);
        });

        btnMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            intent.putExtra("buyerEmail", userEmail);
            startActivity(intent);
        });

        btnFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(this, FeedbackActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });
    }
}