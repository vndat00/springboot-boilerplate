package com.vndat00.springbootboilerplate.payload.response.importexport;

import com.vndat00.springbootboilerplate.importexport.core.FileFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ImportJobCreateResponse(
    UUID jobId,
    String entityType,
    FileFormat fileType,
    List<String> headers,
    List<Map<String, String>> sampleRows,
    List<SuggestedMappingResponse> suggestedMappings) {}
