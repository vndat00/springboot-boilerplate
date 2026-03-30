package com.vndat00.springbootboilerplate.importexport.service;

import com.vndat00.springbootboilerplate.importexport.core.ImportDatasetValidator;
import com.vndat00.springbootboilerplate.importexport.core.ImportRowValidator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ImportValidatorRegistry {
  private final List<ImportRowValidator<?>> rowValidators;
  private final List<ImportDatasetValidator<?>> datasetValidators;

  public ImportValidatorRegistry(
      List<ImportRowValidator<?>> rowValidators,
      List<ImportDatasetValidator<?>> datasetValidators) {
    this.rowValidators = rowValidators;
    this.datasetValidators = datasetValidators;
  }

  public List<ImportRowValidator<?>> rowValidators(String entityType) {
    String normalized = entityType.toLowerCase(Locale.ROOT);
    return rowValidators.stream()
        .filter(v -> v.entityType().toLowerCase(Locale.ROOT).equals(normalized))
        .toList();
  }

  public List<ImportDatasetValidator<?>> datasetValidators(String entityType) {
    String normalized = entityType.toLowerCase(Locale.ROOT);
    return datasetValidators.stream()
        .filter(v -> v.entityType().toLowerCase(Locale.ROOT).equals(normalized))
        .toList();
  }
}
