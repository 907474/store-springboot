package com.myapp.aw.store.model.dto;

import java.math.BigDecimal;

public record TopCustomerDTO(
        Long customerId,
        String username,
        BigDecimal totalSpent
) {}