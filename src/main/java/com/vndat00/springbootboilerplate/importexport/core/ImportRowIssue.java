package com.vndat00.springbootboilerplate.importexport.core;

import java.util.List;

public record ImportRowIssue(
    int rowNumber, ImportRowStatus rowStatus, List<ImportValidationMessage> messages) {}
