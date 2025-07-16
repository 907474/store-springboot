package com.myapp.aw.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "archived_discounts")
public class ArchivedDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderArchive orderArchive;

    private String discountCode;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderArchive getOrderArchive() { return orderArchive; }
    public void setOrderArchive(OrderArchive orderArchive) { this.orderArchive = orderArchive; }
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}