package com.vndat00.springbootboilerplate.payload.request.storage;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageProvider;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageUseCase;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BackendUploadRequest {
  @NotNull private StorageProvider storageProvider;

  @NotNull private StorageUseCase storageUseCase;
}
