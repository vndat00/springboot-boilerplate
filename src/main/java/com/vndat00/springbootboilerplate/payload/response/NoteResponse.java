package com.vndat00.springbootboilerplate.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vndat00.springbootboilerplate.payload.response.storage.StorageObjectResponse;
import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NoteResponse {
  private String id;
  private String note;
  private Integer priority;
  private List<StorageObjectResponse> attachments;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Timestamp deletedAt;
}
