package com.vndat00.springbootboilerplate.importexport.service;

import com.vndat00.springbootboilerplate.importexport.core.ExportColumnDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ExportDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ImportDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ImportFieldDefinition;
import com.vndat00.springbootboilerplate.importexport.definition.ImportDefinitionRegistry;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExportService {
  private final ExportDefinitionRegistry exportDefinitionRegistry;
  private final ImportDefinitionRegistry importDefinitionRegistry;

  public byte[] exportData(String entityType, ExportFormat format) {
    ExportDefinition<?> definition = exportDefinitionRegistry.get(entityType);
    return writeExport(definition, format);
  }

  public byte[] exportTemplate(String entityType, ExportFormat format) {
    ImportDefinition<?> definition = importDefinitionRegistry.get(entityType);
    List<String> headers =
        definition.fields().stream()
            .filter(ImportFieldDefinition::importable)
            .map(ImportFieldDefinition::displayName)
            .toList();

    return switch (format) {
      case CSV -> writeCsv(headers, List.of());
      case XLSX -> writeXlsx(headers, List.of());
    };
  }

  @SuppressWarnings("unchecked")
  private byte[] writeExport(ExportDefinition<?> definition, ExportFormat format) {
    ExportDefinition<Object> typed = (ExportDefinition<Object>) definition;
    List<ExportColumnDefinition<Object>> columns = typed.columns();
    List<String> headers = columns.stream().map(ExportColumnDefinition::headerName).toList();

    List<List<Object>> rows = new ArrayList<>();
    for (Object entity : typed.fetchAll()) {
      List<Object> row = columns.stream().map(column -> column.extractor().apply(entity)).toList();
      rows.add(row);
    }

    return switch (format) {
      case CSV -> writeCsv(headers, rows);
      case XLSX -> writeXlsx(headers, rows);
    };
  }

  private byte[] writeCsv(List<String> headers, List<List<Object>> rows) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVPrinter printer =
            CSVFormat.DEFAULT
                .builder()
                .setHeader(headers.toArray(String[]::new))
                .get()
                .print(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
      for (List<Object> row : rows) {
        printer.printRecord(row);
      }
      printer.flush();
      return outputStream.toByteArray();
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to write csv", ex);
    }
  }

  private byte[] writeXlsx(List<String> headers, List<List<Object>> rows) {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("sheet1");
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < headers.size(); i++) {
        headerRow.createCell(i).setCellValue(headers.get(i));
      }

      int rowNum = 1;
      for (List<Object> rowData : rows) {
        Row row = sheet.createRow(rowNum++);
        for (int col = 0; col < rowData.size(); col++) {
          Object value = rowData.get(col);
          row.createCell(col).setCellValue(value == null ? "" : value.toString());
        }
      }

      workbook.write(outputStream);
      return outputStream.toByteArray();
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to write xlsx", ex);
    }
  }
}
