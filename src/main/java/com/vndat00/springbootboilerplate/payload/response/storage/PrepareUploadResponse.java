package com.vndat00.springbootboilerplate.payload.response.storage;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PrepareUploadResponse {
  private UUID storageObjectId;
  private String uploadUrl;
  private String method;

  private Map<String, String> requiredHeaders;

  // file info
  private String blobKey;
  private String container;

  // constraint
  private Long maxSize;
  private String contentType;

  // expiry
  private Timestamp expiresAt;
}
