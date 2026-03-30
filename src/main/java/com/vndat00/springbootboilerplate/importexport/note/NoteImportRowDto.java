package com.vndat00.springbootboilerplate.importexport.note;

import com.vndat00.springbootboilerplate.domain.enums.NoteStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteImportRowDto {
  @NotBlank
  @Size(max = 255)
  private String title;

  @NotBlank
  @Size(max = 255)
  private String note;

  @Min(1)
  @Max(10)
  private Integer priority;

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
