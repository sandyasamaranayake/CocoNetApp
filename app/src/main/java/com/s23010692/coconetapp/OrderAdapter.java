package com.s23010692.coconetapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orderList;
    private final Context context;
    private final DBHelper dbHelper;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order == null) return;

        holder.txtOrderId.setText("Order ID: " + order.getId());
        holder.txtQuantity.setText("Quantity: " + order.getQuantity());
        holder.txtPrice.setText("Price: Rs " + order.getPrice());
        holder.txtOwner.setText("Farmer: " + order.getOwnerEmail());
        holder.txtLocation.setText("Location: " + (order.getLocation() != null ? order.getLocation() : "N/A"));

        // Display latitude and longitude if available
        holder.txtCoordinates.setText(String.format("Coordinates: %.6f, %.6f", order.getLatitude(), order.getLongitude()));

        String imageUri = order.getImageUri();
        Log.d("OrderAdapter", "Order imageUri: " + imageUri);

        if (imageUri != null && !imageUri.isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(imageUri))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.btnCancel.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < orderList.size()) {
                boolean cancelled = dbHelper.cancelOrder(order.getId());
                if (cancelled) {
                    orderList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, orderList.size());
                    Toast.makeText(context, "Order cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtQuantity, txtPrice, txtOwner, txtLocation, txtCoordinates;
        ImageView imgProduct;
        Button btnCancel;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtOwner = itemView.findViewById(R.id.txtOwner);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtCoordinates = itemView.findViewById(R.id.txtCoordinates); // Add this TextView in XML
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
