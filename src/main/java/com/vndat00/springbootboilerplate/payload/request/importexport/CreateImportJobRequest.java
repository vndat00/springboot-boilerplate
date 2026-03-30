package com.vndat00.springbootboilerplate.payload.request.importexport;

import jakarta.validation.constraints.NotBlank;

public record CreateImportJobRequest(@NotBlank String entityType) {}
