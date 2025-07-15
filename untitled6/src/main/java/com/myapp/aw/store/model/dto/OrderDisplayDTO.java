package com.myapp.aw.store.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDisplayDTO(
        String displayId, // e.g., "T-1" or "F-1"
        Long customerId,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        String status
) {}