package com.myapp.aw.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "archived_product_items")
public class ArchivedProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderArchive orderArchive;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderArchive getOrderArchive() { return orderArchive; }
    public void setOrderArchive(OrderArchive orderArchive) { this.orderArchive = orderArchive; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
}