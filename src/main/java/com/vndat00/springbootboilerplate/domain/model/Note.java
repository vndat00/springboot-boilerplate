package com.vndat00.springbootboilerplate.domain.model;

import com.vndat00.springbootboilerplate.domain.enums.NoteStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notes")
public class Note extends AbstractEntity {
  @Id @GeneratedValue private UUID id;

  @NotBlank private String content;

  @Min(1)
  @Max(10)
  private Integer priority;

  @NotBlank private String title;

  @Email private String authorEmail;

  private String category;

  private BigDecimal estimatedTime;

  private Boolean isCompleted;

  private Boolean isArchived;

  private Integer viewCount;

  private String tags;

  @Enumerated(EnumType.STRING)
  private NoteStatus status;

  private Timestamp dueDate;

  private String description;
}
