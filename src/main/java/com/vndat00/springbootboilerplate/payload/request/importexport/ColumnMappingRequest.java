package com.vndat00.springbootboilerplate.payload.request.importexport;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ColumnMappingRequest(@NotEmpty List<@Valid ColumnMappingItem> mappings) {}
