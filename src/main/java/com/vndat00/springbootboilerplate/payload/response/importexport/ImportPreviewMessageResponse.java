package com.vndat00.springbootboilerplate.payload.response.importexport;

import com.vndat00.springbootboilerplate.importexport.core.Severity;

public record ImportPreviewMessageResponse(
    String field, Severity severity, String code, String message, String rejectedValue) {}
