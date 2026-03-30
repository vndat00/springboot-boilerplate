package com.vndat00.springbootboilerplate.controller;

import com.vndat00.springbootboilerplate.importexport.service.ImportJobService;
import com.vndat00.springbootboilerplate.payload.general.ResponseDataAPI;
import com.vndat00.springbootboilerplate.payload.request.importexport.ColumnMappingRequest;
import com.vndat00.springbootboilerplate.utils.ResponseDataUtils;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/import")
public class ImportController {
  private final ImportJobService importJobService;

  @PostMapping(
      value = "/jobs",
      consumes = {"multipart/form-data"})
  public ResponseEntity<ResponseDataAPI> createJob(
      @RequestParam("entityType") String entityType, @RequestPart("file") MultipartFile file) {
    return ResponseEntity.ok(
        ResponseDataUtils.toResponseData(importJobService.createJob(entityType, file)));
  }

  @GetMapping("/definitions/{entityType}")
  public ResponseEntity<ResponseDataAPI> getDefinition(@PathVariable String entityType) {
    return ResponseEntity.ok(
        ResponseDataUtils.toResponseData(importJobService.getDefinition(entityType)));
  }

  @PostMapping("/jobs/{jobId}/mapping")
  public ResponseEntity<ResponseDataAPI> saveMapping(
      @PathVariable UUID jobId,
      @Valid @org.springframework.web.bind.annotation.RequestBody ColumnMappingRequest request) {
    importJobService.saveMapping(jobId, request);
    return ResponseEntity.ok(ResponseDataUtils.toResponseData());
  }

  @PostMapping("/jobs/{jobId}/preview")
  public ResponseEntity<ResponseDataAPI> preview(@PathVariable UUID jobId) {
    return ResponseEntity.ok(ResponseDataUtils.toResponseData(importJobService.preview(jobId)));
  }

  @GetMapping("/jobs/{jobId}/preview")
  public ResponseEntity<ResponseDataAPI> getPreview(@PathVariable UUID jobId) {
    return ResponseEntity.ok(ResponseDataUtils.toResponseData(importJobService.getPreview(jobId)));
  }

  @PostMapping("/jobs/{jobId}/execute")
  public ResponseEntity<ResponseDataAPI> execute(@PathVariable UUID jobId) {
    return ResponseEntity.ok(ResponseDataUtils.toResponseData(importJobService.execute(jobId)));
  }

  @GetMapping("/jobs/{jobId}/result")
  public ResponseEntity<ResponseDataAPI> result(@PathVariable UUID jobId) {
    return ResponseEntity.ok(ResponseDataUtils.toResponseData(importJobService.getResult(jobId)));
  }
}
