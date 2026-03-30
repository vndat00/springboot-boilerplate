package com.vndat00.springbootboilerplate.payload.response.importexport;

public record ImportJobSummaryResponse(
    int totalRows,
    int validRows,
    int warningRows,
    int errorRows,
    int importedRows,
    int skippedRows) {}
