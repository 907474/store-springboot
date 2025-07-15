package com.myapp.aw.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "temporary_orders")
public class TemporaryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime orderCreationTime;

    private LocalDateTime orderPlacementTime;

    // --- NEW FIELD FOR STATUS ---
    @Enumerated(EnumType.STRING) // Stores the status as a string ("IN_PROGRESS", "FINISHED")
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "temporaryOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TemporaryProductItem> productItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (orderCreationTime == null) {
            orderCreationTime = LocalDateTime.now();
        }
    }

    public void addProductItem(TemporaryProductItem item) {
        productItems.add(item);
        item.setTemporaryOrder(this);
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getOrderCreationTime() { return orderCreationTime; }
    public void setOrderCreationTime(LocalDateTime orderCreationTime) { this.orderCreationTime = orderCreationTime; }
    public LocalDateTime getOrderPlacementTime() { return orderPlacementTime; }
    public void setOrderPlacementTime(LocalDateTime orderPlacementTime) { this.orderPlacementTime = orderPlacementTime; }
    public List<TemporaryProductItem> getProductItems() { return productItems; }
    public void setProductItems(List<TemporaryProductItem> productItems) { this.productItems = productItems; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}