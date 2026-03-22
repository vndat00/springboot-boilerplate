package com.vndat00.springbootboilerplate.controller;

import com.vndat00.springbootboilerplate.payload.general.ResponseDataAPI;
import com.vndat00.springbootboilerplate.payload.request.storage.BackendUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.BindObjectsRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.CompleteUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.PrepareUploadRequest;
import com.vndat00.springbootboilerplate.service.StorageObjectService;
import com.vndat00.springbootboilerplate.utils.ResponseDataUtils;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/storages")
public class StorageObjectController {

  private final StorageObjectService storageObjectService;

  @PostMapping("/prepare")
  public ResponseEntity<ResponseDataAPI> prepareUpload(
      @Valid @RequestBody PrepareUploadRequest request) {
    return ResponseEntity.ok(
        ResponseDataUtils.toResponseData(storageObjectService.prepareClientUpload(request)));
  }

  @PostMapping("/complete")
  public ResponseEntity<ResponseDataAPI> completeUpload(
      @Valid @RequestBody CompleteUploadRequest request) {
    return ResponseEntity.ok(
        ResponseDataUtils.toResponseData(storageObjectService.completeClientUpload(request)));
  }

  @PostMapping(
      value = "/backend",
      consumes = {"multipart/form-data"})
  public ResponseEntity<ResponseDataAPI> uploadViaBackend(
      @RequestPart("file") MultipartFile file,
      @Valid @ModelAttribute BackendUploadRequest request) {
    return ResponseEntity.ok(
        ResponseDataUtils.toResponseData(storageObjectService.uploadViaBackend(file, request)));
  }

  @PostMapping("/objects/bind")
  public ResponseEntity<ResponseDataAPI> bindObjects(
      @Valid @RequestBody BindObjectsRequest request) {
    return ResponseEntity.ok(
        ResponseDataUtils.toResponseData(storageObjectService.bindObjects(request)));
  }

  @DeleteMapping("/objects/{id}")
  public ResponseEntity<ResponseDataAPI> deleteObject(@PathVariable UUID id) {
    boolean deleted = storageObjectService.deleteObject(id);
    if (!deleted) {
      return ResponseEntity.ok(ResponseDataUtils.toResponseData(false));
    }
    return ResponseEntity.ok(ResponseDataUtils.toResponseData());
  }

  @GetMapping("/preview")
  public ResponseEntity<Void> viewFile(
      @RequestParam(required = false) String containerName, @RequestParam String blobKey) {
    try {
      String signedUrl = storageObjectService.getPreviewUrl(containerName, blobKey);
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(URI.create(signedUrl));
      return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
