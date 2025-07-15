package com.myapp.aw.store.model.dto;

import java.math.BigDecimal;

public record StatisticsDTO(
        long totalProducts,
        long totalCustomers,
        long totalFinishedOrders,
        BigDecimal totalRevenue,
        long lowStockProducts
) {}