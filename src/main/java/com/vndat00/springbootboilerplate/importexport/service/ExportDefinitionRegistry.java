package com.vndat00.springbootboilerplate.importexport.service;

import com.vndat00.springbootboilerplate.constant.MessageConstant;
import com.vndat00.springbootboilerplate.exception.NotFoundException;
import com.vndat00.springbootboilerplate.importexport.core.ExportDefinition;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ExportDefinitionRegistry {
  private final Map<String, ExportDefinition<?>> definitions;

  public ExportDefinitionRegistry(List<ExportDefinition<?>> definitions) {
    this.definitions =
        definitions.stream()
            .collect(
                Collectors.toMap(
                    d -> d.entityType().toLowerCase(Locale.ROOT), Function.identity()));
  }

  public ExportDefinition<?> get(String entityType) {
    ExportDefinition<?> definition = definitions.get(entityType.toLowerCase(Locale.ROOT));
    if (definition == null) {
      throw new NotFoundException(MessageConstant.PAGE_NOT_FOUND);
    }
    return definition;
  }
}
