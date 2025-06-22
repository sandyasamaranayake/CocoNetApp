package com.s23010692.coconetapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyProductsActivity extends AppCompatActivity {

    RecyclerView recyclerViewProducts;
    DBHelper dbHelper;
    ProductAdapter productAdapter;
    String farmerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_my_product);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        dbHelper = new DBHelper(this);

        // Get farmer email from intent
        farmerEmail = getIntent().getStringExtra("userEmail");
        if (farmerEmail == null) farmerEmail = "";

        List<Product> productList = dbHelper.getProductsByFarmer(farmerEmail);
        productAdapter = new ProductAdapter(productList, this);

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(productAdapter);
    }
}