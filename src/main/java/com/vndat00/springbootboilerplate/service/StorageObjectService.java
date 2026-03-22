package com.vndat00.springbootboilerplate.service;

import com.vndat00.springbootboilerplate.payload.request.storage.BackendUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.BindObjectsRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.CompleteUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.PrepareUploadRequest;
import com.vndat00.springbootboilerplate.payload.response.storage.PrepareUploadResponse;
import com.vndat00.springbootboilerplate.payload.response.storage.StorageObjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface StorageObjectService {
  PrepareUploadResponse prepareClientUpload(PrepareUploadRequest request);

  StorageObjectResponse completeClientUpload(CompleteUploadRequest request);

  StorageObjectResponse uploadViaBackend(MultipartFile file, BackendUploadRequest request);

  List<StorageObjectResponse> bindObjects(BindObjectsRequest request);

  boolean deleteObject(UUID id);

  String getPreviewUrl(String containerName, String blobKey);
}
