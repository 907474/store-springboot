package com.myapp.aw.store.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record RevenueReportDTO(
        BigDecimal totalRevenue,
        List<ProductRevenueDTO> productRevenueList
) {}