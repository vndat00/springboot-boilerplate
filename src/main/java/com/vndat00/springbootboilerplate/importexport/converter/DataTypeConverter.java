package com.vndat00.springbootboilerplate.importexport.converter;

import com.vndat00.springbootboilerplate.domain.enums.importexport.DataType;
import com.vndat00.springbootboilerplate.importexport.core.ImportValidationMessage;
import com.vndat00.springbootboilerplate.importexport.core.Severity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DataTypeConverter {

  public Object convert(
      String value,
      DataType dataType,
      Class<? extends Enum<?>> enumType,
      int rowNumber,
      String field,
      java.util.List<ImportValidationMessage> messages) {
    if (!StringUtils.hasText(value)) {
      return null;
    }

    try {
      return switch (dataType) {
        case STRING -> value.trim();
        case INTEGER -> NumberImportConverter.toInteger(value, field);
        case DECIMAL -> NumberImportConverter.toBigDecimal(value, field);
        case BOOLEAN -> BooleanImportConverter.toBoolean(value, field);
        case DATE_TIME -> DateTimeImportConverter.toTimestamp(value, field);
        case UUID -> UUIDImportConverter.toUUID(value, field);
        case ENUM -> EnumImportConverter.toEnum(value, field, enumType);
      };
    } catch (Exception ex) {
      messages.add(
          new ImportValidationMessage(
              rowNumber,
              field,
              Severity.ERROR,
              "PARSE_ERROR",
              "Invalid value for " + field + ": " + value,
              value));
      return null;
    }
  }
}
