package com.vndat00.springbootboilerplate.domain.model;

import com.vndat00.springbootboilerplate.importexport.core.FileFormat;
import com.vndat00.springbootboilerplate.importexport.core.ImportJobStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "import_job")
public class ImportJob extends AbstractEntity {
  @Id @GeneratedValue private UUID id;

  private String entityType;

  private String fileName;

  @Enumerated(EnumType.STRING)
  private FileFormat fileType;

  private String sourceFilePath;

  @Enumerated(EnumType.STRING)
  private ImportJobStatus status;

  private Integer totalRows;
  private Integer validRows;
  private Integer warningRows;
  private Integer errorRows;
  private Integer importedRows;
  private Integer skippedRows;
  private String createdBy;
}
