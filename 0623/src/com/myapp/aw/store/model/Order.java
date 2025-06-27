package com.myapp.aw.store.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class Order {
    private long id;
    private long userId;
    private List<OrderItem> items;
    private double totalPrice;
    private LocalDateTime orderDate;
    private String status; // e.g., "COMPLETED", "CANCELLED"

    public Order(long userId, List<OrderItem> items) {
        this.userId = userId;
        this.items = items;
        this.orderDate = LocalDateTime.now();
        this.status = "COMPLETED"; // Default status on creation
        this.totalPrice = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    // --- Getters and Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order{id=%d, userId=%d, total=$%.2f, date=%s, status='%s'}\n",
                id, userId, totalPrice, orderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), status));
        items.forEach(item -> sb.append(item.toString()).append("\n"));
        return sb.toString();
    }
}
