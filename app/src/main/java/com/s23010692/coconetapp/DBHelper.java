package com.s23010692.coconetapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CocoNetApp.db";
    private static final int DATABASE_VERSION = 2; // Updated version

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "role TEXT)");

        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "quantity INTEGER," +
                "location TEXT," +
                "price REAL," +
                "imageUri TEXT," +
                "latitude REAL," +
                "longitude REAL," +
                "ownerEmail TEXT)");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER," +
                "quantity INTEGER," +
                "price REAL," +
                "ownerEmail TEXT," +
                "buyerEmail TEXT," +
                "latitude REAL," + // Added latitude
                "longitude REAL" + // Added longitude
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    public boolean insertUser(String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "email=?", new String[]{email}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        if (exists) return false;

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);
        return db.insert("users", null, values) != -1;
    }

    public Cursor checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("users", null, "email=? AND password=?", new String[]{email, password}, null, null, null);
    }

    public boolean insertProduct(String name, int quantity, String location, double price, String imageUri, String ownerEmail, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("location", location);
        values.put("price", price);
        values.put("imageUri", imageUri);
        values.put("ownerEmail", ownerEmail);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        return db.insert("products", null, values) != -1;
    }

    public List<com.s23010692.coconetapp.Product> getAllProductsWithLocation() {
        List<com.s23010692.coconetapp.Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("products", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
            if (latitude != 0.0 && longitude != 0.0) {
                products.add(extractProduct(cursor));
            }
        }
        cursor.close();
        return products;
    }

    public List<com.s23010692.coconetapp.Product> getAllProducts() {
        List<com.s23010692.coconetapp.Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("products", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            products.add(extractProduct(cursor));
        }
        cursor.close();
        return products;
    }

    public List<com.s23010692.coconetapp.Product> getProductsByFarmer(String farmerEmail) {
        List<com.s23010692.coconetapp.Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("products", null, "ownerEmail=?", new String[]{farmerEmail}, null, null, null);
        while (cursor.moveToNext()) {
            productList.add(extractProduct(cursor));
        }
        cursor.close();
        return productList;
    }

    private com.s23010692.coconetapp.Product extractProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
        String ownerEmail = cursor.getString(cursor.getColumnIndexOrThrow("ownerEmail"));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        return new com.s23010692.coconetapp.Product(id, name, qty, price, imageUri, ownerEmail, location, latitude, longitude);
    }

    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("products", new String[]{"imageUri"}, "id=?", new String[]{String.valueOf(productId)}, null, null, null);
        if (cursor.moveToFirst()) {
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
            if (imageUri != null && imageUri.startsWith("file://")) {
                try {
                    File file = new File(Uri.parse(imageUri).getPath());
                    if (file.exists()) file.delete();
                } catch (Exception e) {
                    Log.e("DBHelper", "Image deletion failed", e);
                }
            }
        }
        cursor.close();
        db.delete("products", "id=?", new String[]{String.valueOf(productId)});
    }

    public boolean updateProductQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        int rows = db.update("products", values, "id=?", new String[]{String.valueOf(productId)});
        return rows > 0;
    }

    public boolean placeOrder(int productId, double price, int quantity, String ownerEmail, String buyerEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get product location
        Cursor cursor = db.query("products", new String[]{"latitude", "longitude"}, "id=?", new String[]{String.valueOf(productId)}, null, null, null);
        double latitude = 0.0;
        double longitude = 0.0;
        if (cursor.moveToFirst()) {
            latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("ownerEmail", ownerEmail);
        values.put("buyerEmail", buyerEmail);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long result = db.insert("orders", null, values);
        return result != -1;
    }

    public List<com.s23010692.coconetapp.Order> getOrdersByBuyer(String buyerEmail) {
        List<com.s23010692.coconetapp.Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o.id, o.quantity, o.price, o.ownerEmail, o.buyerEmail, o.latitude, o.longitude, p.imageUri, p.location " +
                "FROM orders o " +
                "JOIN products p ON o.product_id = p.id " +
                "WHERE o.buyerEmail = ?";
        Cursor cursor = db.rawQuery(query, new String[]{buyerEmail});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String ownerEmail = cursor.getString(cursor.getColumnIndexOrThrow("ownerEmail"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String buyer = cursor.getString(cursor.getColumnIndexOrThrow("buyerEmail"));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            orderList.add(new com.s23010692.coconetapp.Order(id, qty, price, imageUri, ownerEmail, buyer, location, latitude, longitude));
        }
        cursor.close();
        return orderList;
    }

    public boolean cancelOrder(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("orders", "id=?", new String[]{String.valueOf(orderId)}) > 0;
    }
}
