package com.vndat00.springbootboilerplate.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vndat00.springbootboilerplate.domain.enums.NoteStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NoteSearchRequest {
  private String keyword;
  private String content;
  private String title;
  private String authorEmail;
  private String category;
  private String tags;
  private NoteStatus status;
  private Integer priorityFrom;
  private Integer priorityTo;
  private Integer viewCountFrom;
  private Integer viewCountTo;
  private Boolean isCompleted;
  private Boolean isArchived;
  private BigDecimal estimatedTimeFrom;
  private BigDecimal estimatedTimeTo;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime dueDateFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime dueDateTo;

  public boolean isPriorityRangeValid() {
    return priorityFrom == null || priorityTo == null || priorityFrom <= priorityTo;
  }

  public boolean isViewCountRangeValid() {
    return viewCountFrom == null || viewCountTo == null || viewCountFrom <= viewCountTo;
  }

  public boolean isEstimatedTimeRangeValid() {
    return estimatedTimeFrom == null
        || estimatedTimeTo == null
        || estimatedTimeFrom.compareTo(estimatedTimeTo) <= 0;
  }

  public boolean isDueDateRangeValid() {
    return dueDateFrom == null || dueDateTo == null || !dueDateFrom.isAfter(dueDateTo);
  }

  public boolean hasValidRanges() {
    return isPriorityRangeValid()
        && isViewCountRangeValid()
        && isEstimatedTimeRangeValid()
        && isDueDateRangeValid();
  }
}
