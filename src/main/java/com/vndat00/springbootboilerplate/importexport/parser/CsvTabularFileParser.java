package com.vndat00.springbootboilerplate.importexport.parser;

import com.vndat00.springbootboilerplate.importexport.core.FileFormat;
import com.vndat00.springbootboilerplate.importexport.core.ParsedFile;
import com.vndat00.springbootboilerplate.importexport.core.ParsedRow;
import com.vndat00.springbootboilerplate.importexport.core.TabularFileParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvTabularFileParser implements TabularFileParser {

  @Override
  public boolean supports(String fileName) {
    return fileName != null && fileName.toLowerCase().endsWith(".csv");
  }

  @Override
  public FileFormat fileFormat() {
    return FileFormat.CSV;
  }

  @Override
  public ParsedFile parse(InputStream inputStream) throws IOException {
    List<String> headers = new ArrayList<>();
    List<ParsedRow> rows = new ArrayList<>();

    try (CSVParser parser =
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .get()
            .parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      headers.addAll(parser.getHeaderNames());
      for (CSVRecord record : parser) {
        Map<String, String> cells = new LinkedHashMap<>();
        for (String header : headers) {
          cells.put(header, record.isMapped(header) ? record.get(header) : null);
        }
        rows.add(new ParsedRow((int) record.getRecordNumber() + 1, cells));
      }
    }

    return new ParsedFile(fileFormat(), headers, rows);
  }
}
