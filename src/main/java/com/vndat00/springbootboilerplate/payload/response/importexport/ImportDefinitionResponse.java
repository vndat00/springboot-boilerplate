package com.vndat00.springbootboilerplate.payload.response.importexport;

import java.util.List;

public record ImportDefinitionResponse(
    String entityType, List<ImportFieldMetadataResponse> fields) {}
