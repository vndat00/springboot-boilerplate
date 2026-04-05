package com.vndat00.springbootboilerplate.payload.response.importexport;

import com.vndat00.springbootboilerplate.domain.enums.importexport.DataType;
import java.util.List;

public record ImportFieldMetadataResponse(
    String fieldName,
    String displayName,
    DataType dataType,
    boolean required,
    boolean importable,
    boolean exportable,
    List<String> enumValues) {}
