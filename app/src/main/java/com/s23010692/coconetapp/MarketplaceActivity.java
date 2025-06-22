package com.s23010692.coconetapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DBHelper dbHelper;
    String buyerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_market_place);

        recyclerView = findViewById(R.id.recyclerViewMarketplace);
        dbHelper = new DBHelper(this);


        buyerEmail = getIntent().getStringExtra("buyerEmail");
        if (buyerEmail == null || buyerEmail.isEmpty()) {
            Toast.makeText(this, "Missing buyer email. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        List<Product> productList = dbHelper.getAllProducts();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MarketplaceAdapter(productList));
    }

    class MarketplaceAdapter extends RecyclerView.Adapter<MarketplaceAdapter.ProductViewHolder> {

        List<Product> products;

        MarketplaceAdapter(List<Product> products) {
            this.products = products;
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView txtQuantity, txtPrice, txtOwner, txtLocation;
            ImageView imgProduct;
            Button btnOrder;

            ProductViewHolder(View itemView) {
                super(itemView);
                txtQuantity = itemView.findViewById(R.id.txtQuantity);
                txtPrice = itemView.findViewById(R.id.txtPrice);
                txtOwner = itemView.findViewById(R.id.txtOwner);
                txtLocation = itemView.findViewById(R.id.txtLocation);
                imgProduct = itemView.findViewById(R.id.imgProduct);
                btnOrder = itemView.findViewById(R.id.btnContact);
            }
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marketplace, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = products.get(position);

            holder.txtQuantity.setText("Quantity: " + product.getQuantity());
            holder.txtPrice.setText("Price: Rs " + product.getPrice());
            holder.txtOwner.setText("Farmer: " + product.getOwnerEmail());
            holder.txtLocation.setText("Location: " + product.getLocation());

            String imageUri = product.getImageUri();
            if (imageUri != null && !imageUri.isEmpty()) {
                Glide.with(holder.imgProduct.getContext())
                        .load(Uri.parse(imageUri))
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
            }

            holder.btnOrder.setText("Order");
            holder.btnOrder.setOnClickListener(v -> {
                int quantity = product.getQuantity();
                if (quantity <= 0) {
                    Toast.makeText(MarketplaceActivity.this, "Out of stock!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int orderQuantity = 1; // static for now
                int newQuantity = quantity - orderQuantity;

                boolean success = dbHelper.placeOrder(
                        product.getId(),
                        product.getPrice(),
                        orderQuantity,
                        product.getOwnerEmail(),
                        buyerEmail
                );

                if (success) {
                    boolean updated = dbHelper.updateProductQuantity(product.getId(), newQuantity);
                    if (updated) {
                        product.setQuantity(newQuantity);
                        notifyItemChanged(holder.getAdapterPosition());
                        Toast.makeText(MarketplaceActivity.this, "Order placed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MarketplaceActivity.this, "Order placed, but failed to update quantity!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MarketplaceActivity.this, "Failed to place order.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }
}