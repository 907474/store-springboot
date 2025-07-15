package com.myapp.aw.store.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private long id;
    private long userId;
    private List<OrderItem> items;
    private double totalPrice;
    private LocalDateTime orderStartedAt; // The new field
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private String status;

    // This constructor for creating new orders is correct from our last change.
    public Order(long userId, List<OrderItem> items, LocalDateTime orderStartedAt) {
        this.userId = userId;
        this.items = items;
        this.orderStartedAt = orderStartedAt;
        this.createdAt = LocalDateTime.now();
        this.confirmedAt = this.createdAt;
        this.status = "COMPLETED";
        this.totalPrice = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    // THIS CONSTRUCTOR IS THE FIX. It now accepts the new timestamp.
    public Order(long id, long userId, double totalPrice, LocalDateTime orderStartedAt, LocalDateTime createdAt, LocalDateTime confirmedAt, String status) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderStartedAt = orderStartedAt; // ADD THIS LINE
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
        this.status = status;
        this.items = new ArrayList<>();
    }

    // --- Getters and Setters ---

    public LocalDateTime getOrderStartedAt() { return orderStartedAt; }
    public void setOrderStartedAt(LocalDateTime orderStartedAt) { this.orderStartedAt = orderStartedAt; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}