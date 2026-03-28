package com.vndat00.springbootboilerplate.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vndat00.springbootboilerplate.domain.enums.NoteStatus;
import com.vndat00.springbootboilerplate.payload.response.storage.StorageObjectResponse;
import java.math.BigDecimal;
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
  private String title;
  private String authorEmail;
  private String category;
  private BigDecimal estimatedTime;
  private Boolean isCompleted;
  private Boolean isArchived;
  private Integer viewCount;
  private String tags;
  private NoteStatus status;
  private Timestamp dueDate;
  private String description;
}
