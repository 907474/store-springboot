package com.myapp.aw.store.model.dto;

import java.util.List;

public record BulkUpdateResultDTO(
        List<String> updatedDetails,
        List<String> createdProductNames,
        List<String> errors
) {}