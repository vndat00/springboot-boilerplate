package com.vndat00.springbootboilerplate.domain.model;

import com.vndat00.springbootboilerplate.importexport.core.ImportRowStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "import_job_row")
public class ImportJobRow extends AbstractEntity {
  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", nullable = false)
  private ImportJob job;

  private Integer rowNumber;

  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> rawPayloadJson;

  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> mappedPayloadJson;

  @Enumerated(EnumType.STRING)
  private ImportRowStatus rowStatus;
}
