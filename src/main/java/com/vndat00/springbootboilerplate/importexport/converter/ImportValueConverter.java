package com.vndat00.springbootboilerplate.importexport.converter;

import com.vndat00.springbootboilerplate.importexport.core.ImportFieldDefinition;

public final class ImportValueConverter {

  private ImportValueConverter() {}

  public static Object convert(Object rawValue, ImportFieldDefinition fieldDefinition) {
    if (fieldDefinition == null) {
      throw new IllegalArgumentException("fieldDefinition must not be null");
    }

    return switch (fieldDefinition.dataType()) {
      case STRING -> toStringValue(rawValue);
      case INTEGER -> NumberImportConverter.toInteger(rawValue, fieldDefinition.fieldName());
      case DECIMAL -> NumberImportConverter.toBigDecimal(rawValue, fieldDefinition.fieldName());
      case BOOLEAN -> BooleanImportConverter.toBoolean(rawValue, fieldDefinition.fieldName());
      case ENUM ->
          EnumImportConverter.toEnum(
              rawValue, fieldDefinition.fieldName(), castEnumClass(fieldDefinition.enumType()));
      case DATE_TIME -> DateTimeImportConverter.toTimestamp(rawValue, fieldDefinition.fieldName());
      case UUID -> UUIDImportConverter.toUUID(rawValue, fieldDefinition.fieldName());
    };
  }

  private static String toStringValue(Object rawValue) {
    if (rawValue == null) {
      return null;
    }
    String value = rawValue.toString().trim();
    return value.isBlank() ? null : value;
  }

  @SuppressWarnings("unchecked")
  private static <E extends Enum<E>> Class<E> castEnumClass(Class<?> enumClass) {
    if (enumClass == null) {
      throw new IllegalArgumentException("enumClass must not be null for ENUM field");
    }
    return (Class<E>) enumClass;
  }
}
