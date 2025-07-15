package com.myapp.aw.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "temporary_discounts")
public class TemporaryDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TemporaryOrder temporaryOrder;

    private String discountCode;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TemporaryOrder getTemporaryOrder() { return temporaryOrder; }
    public void setTemporaryOrder(TemporaryOrder temporaryOrder) { this.temporaryOrder = temporaryOrder; }
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}