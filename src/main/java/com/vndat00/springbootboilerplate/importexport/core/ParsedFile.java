package com.vndat00.springbootboilerplate.importexport.core;

import java.util.List;

public record ParsedFile(FileFormat fileFormat, List<String> headers, List<ParsedRow> rows) {
  public List<ParsedRow> sampleRows(int maxRows) {
    return rows.stream().limit(maxRows).toList();
  }
}
