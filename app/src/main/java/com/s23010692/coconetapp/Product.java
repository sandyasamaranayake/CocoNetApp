package com.s23010692.coconetapp;

public class Product {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private String imageUri;
    private String ownerEmail;
    private String locationText;   // Human-readable location
    private double latitude;
    private double longitude;

    // Constructor
    public Product(int id, String name, int quantity, double price, String imageUri,
                   String ownerEmail, String locationText, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUri = imageUri;
        this.ownerEmail = ownerEmail;
        this.locationText = locationText;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getLocationText() {
        return locationText;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Returns the human-readable location
    public String getLocation() {
        return locationText;
    }
}