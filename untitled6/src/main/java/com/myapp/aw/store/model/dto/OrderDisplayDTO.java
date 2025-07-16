package com.myapp.aw.store.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDisplayDTO(
        String displayId,
        Long customerId,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        String status
) {}