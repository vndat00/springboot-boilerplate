package com.vndat00.springbootboilerplate.importexport.parser;

import com.vndat00.springbootboilerplate.importexport.core.FileFormat;
import com.vndat00.springbootboilerplate.importexport.core.ParsedFile;
import com.vndat00.springbootboilerplate.importexport.core.ParsedRow;
import com.vndat00.springbootboilerplate.importexport.core.TabularFileParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelTabularFileParser implements TabularFileParser {

  @Override
  public boolean supports(String fileName) {
    return fileName != null && fileName.toLowerCase().endsWith(".xlsx");
  }

  @Override
  public FileFormat fileFormat() {
    return FileFormat.XLSX;
  }

  @Override
  public ParsedFile parse(InputStream inputStream) throws IOException {
    List<String> headers = new ArrayList<>();
    List<ParsedRow> rows = new ArrayList<>();
    DataFormatter formatter = new DataFormatter();

    try (Workbook workbook = new XSSFWorkbook(inputStream)) {
      Sheet sheet = workbook.getSheetAt(0);
      Row headerRow = sheet.getRow(sheet.getFirstRowNum());
      if (headerRow == null) {
        return new ParsedFile(fileFormat(), headers, rows);
      }

      for (Cell headerCell : headerRow) {
        headers.add(formatter.formatCellValue(headerCell));
      }

      for (int i = headerRow.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) {
          continue;
        }
        Map<String, String> cells = new LinkedHashMap<>();
        for (int c = 0; c < headers.size(); c++) {
          Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
          cells.put(headers.get(c), cell == null ? null : formatter.formatCellValue(cell));
        }
        rows.add(new ParsedRow(i + 1, cells));
      }
    }

    return new ParsedFile(fileFormat(), headers, rows);
  }
}
