package com.s23010692.coconetapp;

import java.io.Serializable;

/**
 * Represents an order in the system.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int quantity;
    private double price;
    private String imageUri;
    private String ownerEmail;
    private String buyerEmail;
    private String location;
    private double latitude;
    private double longitude;

    public Order(int id, int quantity, double price, String imageUri, String ownerEmail, String buyerEmail, String location, double latitude, double longitude) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.imageUri = imageUri;
        this.ownerEmail = ownerEmail;
        this.buyerEmail = buyerEmail;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", price=" + price +
                ", imageUri='" + imageUri + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", buyerEmail='" + buyerEmail + '\'' +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
