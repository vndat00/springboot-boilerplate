package com.vndat00.springbootboilerplate.controller;

import com.vndat00.springbootboilerplate.importexport.service.ExportFormat;
import com.vndat00.springbootboilerplate.importexport.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export")
public class ExportController {
  private final ExportService exportService;

  @GetMapping("/{entityType}")
  public ResponseEntity<byte[]> exportData(
      @PathVariable String entityType, @RequestParam(defaultValue = "csv") String format) {
    ExportFormat exportFormat = ExportFormat.fromValue(format);
    byte[] content = exportService.exportData(entityType, exportFormat);
    return buildFileResponse(entityType + "-data", exportFormat, content);
  }

  @GetMapping("/{entityType}/template")
  public ResponseEntity<byte[]> exportTemplate(
      @PathVariable String entityType, @RequestParam(defaultValue = "csv") String format) {
    ExportFormat exportFormat = ExportFormat.fromValue(format);
    byte[] content = exportService.exportTemplate(entityType, exportFormat);
    return buildFileResponse(entityType + "-template", exportFormat, content);
  }

  private ResponseEntity<byte[]> buildFileResponse(
      String fileNamePrefix, ExportFormat format, byte[] content) {
    String extension = format == ExportFormat.CSV ? "csv" : "xlsx";
    MediaType mediaType =
        format == ExportFormat.CSV
            ? MediaType.TEXT_PLAIN
            : MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(fileNamePrefix + "." + extension)
                .build()
                .toString())
        .contentType(mediaType)
        .body(content);
  }
}
