package com.vndat00.springbootboilerplate.payload.request.importexport;

import jakarta.validation.constraints.NotBlank;

public record ColumnMappingItem(@NotBlank String sourceColumn, @NotBlank String targetField) {}
