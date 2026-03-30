package com.vndat00.springbootboilerplate.importexport.service;

import com.vndat00.springbootboilerplate.importexport.core.DataType;
import com.vndat00.springbootboilerplate.importexport.core.ImportValidationMessage;
import com.vndat00.springbootboilerplate.importexport.core.Severity;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
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
        case INTEGER -> Integer.valueOf(value.trim());
        case DECIMAL -> new BigDecimal(value.trim());
        case BOOLEAN -> parseBoolean(value.trim());
        case DATE_TIME -> parseTimestamp(value.trim());
        case UUID -> java.util.UUID.fromString(value.trim());
        case ENUM -> parseEnum(value.trim(), enumType);
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

  private Boolean parseBoolean(String value) {
    String normalized = value.toLowerCase(Locale.ROOT);
    return switch (normalized) {
      case "true", "1", "yes", "y" -> true;
      case "false", "0", "no", "n" -> false;
      default -> throw new IllegalArgumentException("Invalid boolean");
    };
  }

  private Timestamp parseTimestamp(String value) {
    try {
      return Timestamp.valueOf(LocalDateTime.parse(value));
    } catch (DateTimeParseException ex) {
      return Timestamp.from(OffsetDateTime.parse(value).toInstant());
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Enum<?> parseEnum(String value, Class<? extends Enum<?>> enumType) {
    if (enumType == null) {
      throw new IllegalArgumentException("Enum type is required");
    }
    return Enum.valueOf((Class) enumType, value.trim().toUpperCase(Locale.ROOT));
  }

  public java.util.List<String> enumValues(Class<? extends Enum<?>> enumType) {
    if (enumType == null) {
      return java.util.List.of();
    }
    return Arrays.stream(enumType.getEnumConstants()).map(Enum::name).toList();
  }
}
