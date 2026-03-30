package com.vndat00.springbootboilerplate.importexport.core;

import java.util.List;

public interface ExportDefinition<TEntity> {
  String entityType();

  List<ExportColumnDefinition<TEntity>> columns();

  List<TEntity> fetchAll();
}
