package com.vndat00.springbootboilerplate.mapper;

import com.vndat00.springbootboilerplate.config.SpringMapStructConfig;
import com.vndat00.springbootboilerplate.config.properties.ServerProperties;
import com.vndat00.springbootboilerplate.domain.model.StorageObject;
import com.vndat00.springbootboilerplate.payload.request.storage.BackendUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.PrepareUploadRequest;
import com.vndat00.springbootboilerplate.payload.response.storage.StorageObjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapStructConfig.class)
public interface StorageObjectMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "blobKey", ignore = true)
  @Mapping(target = "containerName", ignore = true)
  @Mapping(target = "actualSize", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "uploadMethod", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "note", ignore = true)
  StorageObject toEntity(PrepareUploadRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "blobKey", ignore = true)
  @Mapping(target = "containerName", ignore = true)
  @Mapping(target = "fileName", ignore = true)
  @Mapping(target = "contentType", ignore = true)
  @Mapping(target = "declaredSize", ignore = true)
  @Mapping(target = "actualSize", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "uploadMethod", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "note", ignore = true)
  StorageObject toEntity(BackendUploadRequest request);

  @Mapping(target = "url", expression = "java(getUrl(object))")
  StorageObjectResponse toResponse(StorageObject object);

  default String getUrl(StorageObject storageObject) {
    if (storageObject != null && storageObject.getBlobKey() != null) {
      if (storageObject.getBlobKey().contains(ServerProperties.getServerUrl())) {
        return ServerProperties.getServerVersion()
            + "/storages/preview?containerName="
            + storageObject.getContainerName()
            + "&blobKey="
            + storageObject.getBlobKey();
      } else {
        return ServerProperties.getServerUrl()
            + ServerProperties.getServerVersion()
            + "/storages/preview?containerName="
            + storageObject.getContainerName()
            + "&blobKey="
            + storageObject.getBlobKey();
      }
    }
    return null;
  }
}
