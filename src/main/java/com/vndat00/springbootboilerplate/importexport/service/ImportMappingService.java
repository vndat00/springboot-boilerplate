package com.vndat00.springbootboilerplate.importexport.service;

import com.vndat00.springbootboilerplate.importexport.core.ImportFieldDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ImportValidationMessage;
import com.vndat00.springbootboilerplate.importexport.core.Severity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ImportMappingService {
  private final DataTypeConverter dataTypeConverter;

  public ImportMappingService(DataTypeConverter dataTypeConverter) {
    this.dataTypeConverter = dataTypeConverter;
  }

  public List<ImportValidationMessage> validateMappings(
      List<ImportFieldDefinition> fieldDefinitions,
      Map<String, String> mapping,
      List<String> headers) {
    Map<String, ImportFieldDefinition> fieldMap =
        fieldDefinitions.stream()
            .collect(Collectors.toMap(ImportFieldDefinition::fieldName, Function.identity()));

    Set<String> duplicatedTargets =
        mapping.values().stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

    List<ImportValidationMessage> messages = new java.util.ArrayList<>();

    for (String duplicateTarget : duplicatedTargets) {
      messages.add(
          new ImportValidationMessage(
              0,
              duplicateTarget,
              Severity.ERROR,
              "DUPLICATE_MAPPING",
              "Target field is mapped multiple times",
              duplicateTarget));
    }

    for (Map.Entry<String, String> entry : mapping.entrySet()) {
      if (!headers.contains(entry.getKey())) {
        messages.add(
            new ImportValidationMessage(
                0,
                entry.getKey(),
                Severity.ERROR,
                "SOURCE_COLUMN_NOT_FOUND",
                "Source column not found in uploaded file",
                entry.getKey()));
      }
      if (!fieldMap.containsKey(entry.getValue())) {
        messages.add(
            new ImportValidationMessage(
                0,
                entry.getValue(),
                Severity.ERROR,
                "TARGET_FIELD_NOT_FOUND",
                "Target field not found in import definition",
                entry.getValue()));
      }
    }

    fieldDefinitions.stream()
        .filter(ImportFieldDefinition::required)
        .forEach(
            field -> {
              boolean mapped = mapping.containsValue(field.fieldName());
              if (!mapped) {
                messages.add(
                    new ImportValidationMessage(
                        0,
                        field.fieldName(),
                        Severity.ERROR,
                        "MISSING_REQUIRED_MAPPING",
                        "Required field is not mapped",
                        null));
              }
            });

    return messages;
  }

  public Map<String, Object> mapRow(
      int rowNumber,
      Map<String, String> sourceRow,
      Map<String, String> mapping,
      List<ImportFieldDefinition> fieldDefinitions,
      List<ImportValidationMessage> messages) {
    Map<String, ImportFieldDefinition> definitionMap =
        fieldDefinitions.stream()
            .collect(Collectors.toMap(ImportFieldDefinition::fieldName, Function.identity()));
    Map<String, Object> mappedPayload = new HashMap<>();

    for (Map.Entry<String, String> entry : mapping.entrySet()) {
      String sourceColumn = entry.getKey();
      String targetField = entry.getValue();
      ImportFieldDefinition fieldDefinition = definitionMap.get(targetField);
      if (fieldDefinition == null) {
        continue;
      }
      String raw = sourceRow.get(sourceColumn);
      Object converted =
          dataTypeConverter.convert(
              raw,
              fieldDefinition.dataType(),
              fieldDefinition.enumType(),
              rowNumber,
              targetField,
              messages);
      mappedPayload.put(targetField, converted);

      if (fieldDefinition.required() && converted == null) {
        messages.add(
            new ImportValidationMessage(
                rowNumber,
                targetField,
                Severity.ERROR,
                "REQUIRED_FIELD_EMPTY",
                "Required field is empty",
                raw));
      }
    }

    return mappedPayload;
  }
}
