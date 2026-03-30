package com.vndat00.springbootboilerplate.payload.response.importexport;

import java.util.List;
import java.util.UUID;

public record ImportPreviewResponse(
    UUID jobId, ImportJobSummaryResponse summary, List<ImportPreviewRowResponse> rows) {}
