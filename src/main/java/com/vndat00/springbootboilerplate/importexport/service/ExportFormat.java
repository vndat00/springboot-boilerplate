package com.vndat00.springbootboilerplate.importexport.service;

public enum ExportFormat {
  CSV,
  XLSX;

  public static ExportFormat fromValue(String value) {
    if (value == null) {
      return CSV;
    }
    return ExportFormat.valueOf(value.trim().toUpperCase());
  }
}
