package com.vndat00.springbootboilerplate.importexport.core;

import java.util.function.Function;

public record ExportColumnDefinition<TEntity>(
    String fieldName, String headerName, DataType dataType, Function<TEntity, Object> extractor) {}
