package com.vndat00.springbootboilerplate.payload.response.importexport;

import com.vndat00.springbootboilerplate.importexport.core.ImportRowStatus;
import java.util.List;

public record ImportPreviewRowResponse(
    int rowNumber, ImportRowStatus status, List<ImportPreviewMessageResponse> messages) {}
