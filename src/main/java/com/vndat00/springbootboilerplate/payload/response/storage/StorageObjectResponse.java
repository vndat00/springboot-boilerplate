package com.vndat00.springbootboilerplate.payload.response.storage;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageStatus;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageUseCase;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.UploadMethod;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StorageObjectResponse {
  private UUID id;
  private String blobKey;
  private String url;
  private String containerName;
  private String fileName;
  private String contentType;
  private Long declaredSize;
  private Long actualSize;
  private StorageStatus status;
  private UploadMethod uploadMethod;
  private StorageUseCase storageUseCase;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Timestamp deletedAt;
}
