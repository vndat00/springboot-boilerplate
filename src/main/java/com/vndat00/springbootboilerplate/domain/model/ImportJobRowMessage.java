package com.vndat00.springbootboilerplate.domain.model;

import com.vndat00.springbootboilerplate.importexport.core.Severity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "import_job_row_message")
public class ImportJobRowMessage extends AbstractEntity {
  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_row_id", nullable = false)
  private ImportJobRow jobRow;

  private String fieldName;

  @Enumerated(EnumType.STRING)
  private Severity severity;

  private String code;

  private String message;

  private String rejectedValue;
}
