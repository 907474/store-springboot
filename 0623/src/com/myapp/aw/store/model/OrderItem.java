package com.myapp.aw.store.model;

public class OrderItem {
    private long productId;
    private String productName;
    private int quantity;
    private double priceAtPurchase;

    public OrderItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.quantity = quantity;
        this.priceAtPurchase = product.getPrice();
    }

    public long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPriceAtPurchase() { return priceAtPurchase; }
    public double getSubtotal() { return quantity * priceAtPurchase; }

    @Override
    public String toString() {
        return String.format("  - Item: %-20s | Qty: %-3d | Price: $%-7.2f | Subtotal: $%.2f",
                productName, quantity, priceAtPurchase, getSubtotal());
    }
}