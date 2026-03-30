package com.vndat00.springbootboilerplate.importexport.core;

import java.util.List;

public interface ImportRowValidator<T> {
  String entityType();

  List<ImportValidationMessage> validate(T row, int rowNumber);
}
