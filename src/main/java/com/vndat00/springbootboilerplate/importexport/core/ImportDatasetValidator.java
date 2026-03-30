package com.vndat00.springbootboilerplate.importexport.core;

import java.util.List;

public interface ImportDatasetValidator<T> {
  String entityType();

  List<ImportValidationMessage> validate(List<T> rows);
}
