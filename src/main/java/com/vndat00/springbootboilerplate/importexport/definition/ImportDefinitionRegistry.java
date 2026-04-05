package com.vndat00.springbootboilerplate.importexport.definition;

import com.vndat00.springbootboilerplate.constant.MessageConstant;
import com.vndat00.springbootboilerplate.exception.NotFoundException;
import com.vndat00.springbootboilerplate.importexport.core.ImportDefinition;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ImportDefinitionRegistry {
  private final Map<String, ImportDefinition<?>> definitions;

  public ImportDefinitionRegistry(List<ImportDefinition<?>> definitions) {
    this.definitions =
        definitions.stream()
            .collect(
                Collectors.toMap(
                    d -> d.entityType().toLowerCase(Locale.ROOT), Function.identity()));
  }

  public ImportDefinition<?> get(String entityType) {
    ImportDefinition<?> definition = definitions.get(entityType.toLowerCase(Locale.ROOT));
    if (definition == null) {
      throw new NotFoundException(MessageConstant.PAGE_NOT_FOUND);
    }
    return definition;
  }
}
