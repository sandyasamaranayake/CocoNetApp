package com.s23010692.coconetapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;

import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private TextView txtNoOrders;
    private DBHelper dbHelper;
    private String buyerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_my_orders);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        dbHelper = new DBHelper(this);

        buyerEmail = getIntent().getStringExtra("buyerEmail");
        if (buyerEmail == null || buyerEmail.isEmpty()) {
            Toast.makeText(this, "Missing buyer email. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<Order> orderList = dbHelper.getOrdersByBuyer(buyerEmail);

        if (orderList.isEmpty()) {
            txtNoOrders.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
        } else {
            txtNoOrders.setVisibility(View.GONE);
            recyclerViewOrders.setVisibility(View.VISIBLE);
            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewOrders.setAdapter(new OrdersAdapter(orderList));
        }
    }

    class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

        private List<Order> orderList;

        OrdersAdapter(List<Order> orderList) {
            this.orderList = orderList;
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView txtOrderId, txtQuantity, txtPrice, txtOwner, txtLocation;
            ImageView imgProduct;
            Button btnCancel;

            OrderViewHolder(View itemView) {
                super(itemView);
                txtOrderId = itemView.findViewById(R.id.txtOrderId);
                txtQuantity = itemView.findViewById(R.id.txtQuantity);
                txtPrice = itemView.findViewById(R.id.txtPrice);
                txtOwner = itemView.findViewById(R.id.txtOwner);
                txtLocation = itemView.findViewById(R.id.txtLocation);
                imgProduct = itemView.findViewById(R.id.imgProduct);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orderList.get(position);
            holder.txtOrderId.setText("Order ID: " + order.getId());
            holder.txtQuantity.setText("Quantity: " + order.getQuantity());
            holder.txtPrice.setText("Price: Rs " + order.getPrice());
            holder.txtOwner.setText("Farmer: " + order.getOwnerEmail());
            holder.txtLocation.setText("Location: " + order.getLocation());

            if (order.getImageUri() != null && !order.getImageUri().isEmpty()) {
                Glide.with(holder.imgProduct.getContext())
                        .load(Uri.parse(order.getImageUri()))
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
                
            }

            holder.btnCancel.setOnClickListener(v -> {
                boolean cancelled = dbHelper.cancelOrder(order.getId());
                if (cancelled) {
                    orderList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, orderList.size());
                    Toast.makeText(MyOrdersActivity.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyOrdersActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }
}
