package com.myapp.aw.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "temporary_product_items")
public class TemporaryProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TemporaryOrder temporaryOrder;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TemporaryOrder getTemporaryOrder() { return temporaryOrder; }
    public void setTemporaryOrder(TemporaryOrder temporaryOrder) { this.temporaryOrder = temporaryOrder; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
}