package com.myapp.aw.store.model.dto;

import java.math.BigDecimal;

public record ProductRevenueDTO(
        String productName,
        int quantitySold,
        BigDecimal totalRevenue
) {}