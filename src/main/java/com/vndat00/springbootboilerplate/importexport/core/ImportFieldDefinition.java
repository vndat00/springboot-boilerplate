package com.vndat00.springbootboilerplate.importexport.core;

import com.vndat00.springbootboilerplate.domain.enums.importexport.DataType;
import java.util.List;

public record ImportFieldDefinition(
    String fieldName,
    String displayName,
    DataType dataType,
    boolean required,
    boolean importable,
    boolean exportable,
    List<String> enumValues,
    Class<? extends Enum<?>> enumType) {}
