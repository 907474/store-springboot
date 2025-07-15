package com.myapp.aw.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_archives")
public class OrderArchive {

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

    @OneToMany(
            mappedBy = "orderArchive",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ArchivedProductItem> productItems = new ArrayList<>();

    @OneToMany(
            mappedBy = "orderArchive",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ArchivedDiscount> discounts = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        orderCreationTime = LocalDateTime.now();
    }

    public void addProductItem(ArchivedProductItem item) {
        productItems.add(item);
        item.setOrderArchive(this);
    }

    public void addDiscount(ArchivedDiscount discount) {
        discounts.add(discount);
        discount.setOrderArchive(this);
    }

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
    public List<ArchivedProductItem> getProductItems() { return productItems; }
    public void setProductItems(List<ArchivedProductItem> productItems) { this.productItems = productItems; }
    public List<ArchivedDiscount> getDiscounts() { return discounts; }
    public void setDiscounts(List<ArchivedDiscount> discounts) { this.discounts = discounts; }
}