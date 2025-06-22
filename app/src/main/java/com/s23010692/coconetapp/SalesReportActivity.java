package com.s23010692.coconetapp;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
public class SalesReportActivity extends AppCompatActivity {
    TextView tvTotalProducts, tvTotalQuantity, tvTotalSales;
    DBHelper dbHelper;
    String farmerEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_sales_report);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalQuantity = findViewById(R.id.tvTotalQuantity);
        tvTotalSales = findViewById(R.id.tvTotalSales);

        dbHelper = new DBHelper(this);

        farmerEmail = getIntent().getStringExtra("userEmail");
        if (farmerEmail == null) farmerEmail = "";
        List<Product> productList = dbHelper.getProductsByFarmer(farmerEmail);
        int totalProducts = productList.size();
        int totalQuantity = 0;
        double totalSales = 0.0;

        for (Product product : productList) {
            totalQuantity += product.getQuantity();
            totalSales += product.getQuantity() * product.getPrice();
        }
        tvTotalProducts.setText("Total Products: " + totalProducts);
        tvTotalQuantity.setText("Total Quantity: " + totalQuantity);
        tvTotalSales.setText("Total Sales: Rs " + String.format("%.2f", totalSales));
    }
}