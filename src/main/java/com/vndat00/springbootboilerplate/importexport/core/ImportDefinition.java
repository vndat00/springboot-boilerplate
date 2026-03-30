package com.vndat00.springbootboilerplate.importexport.core;

import java.util.List;
import java.util.Map;

public interface ImportDefinition<T> {
  String entityType();

  Class<T> dtoType();

  List<ImportFieldDefinition> fields();

  T toDto(Map<String, Object> mappedValues);

  void saveChunk(List<T> rows);
}
