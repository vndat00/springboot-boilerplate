package com.vndat00.springbootboilerplate.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vndat00.springbootboilerplate.domain.enums.NoteStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NoteRequest {
  @NotBlank
  @Size(max = 255)
  private String note;

  @Min(1)
  @Max(10)
  private Integer priority;

  private List<UUID> attachmentFileIds;

  @NotBlank private String title;

  @Email private String authorEmail;

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
