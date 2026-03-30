package com.vndat00.springbootboilerplate.importexport.core;

public record ImportValidationMessage(
    int rowNumber,
    String field,
    Severity severity,
    String code,
    String message,
    String rejectedValue) {}
