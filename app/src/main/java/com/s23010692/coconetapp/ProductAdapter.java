package com.s23010692.coconetapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final Context context;
    private final DBHelper dbHelper;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.txtQuantity.setText("Quantity: " + product.getQuantity());
        holder.txtPrice.setText("Price: Rs " + product.getPrice());
        holder.txtLocation.setText("Location: " + product.getLocation());

        String imageUri = product.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(imageUri))
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.deleteProduct(product.getId());
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                productList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, productList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuantity, txtPrice, txtLocation;
        ImageView imgProduct;
        Button btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtLocation = itemView.findViewById(R.id.txtLocation); // Fixed id
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}