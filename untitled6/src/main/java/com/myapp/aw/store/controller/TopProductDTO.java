package com.myapp.aw.store.model.dto;

public record TopProductDTO(
        String productName,
        String productSku,
        int totalQuantitySold
) {}