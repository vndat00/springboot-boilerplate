package com.vndat00.springbootboilerplate.importexport.converter;

public final class EnumImportConverter {

  private EnumImportConverter() {}

  public static Enum<?> toEnum(Object value, String fieldName, Class<? extends Enum<?>> enumClass) {
    if (value == null) {
      return null;
    }

    if (enumClass.isInstance(value)) {
      return (Enum<?>) value;
    }

    if (value instanceof String raw) {
      String normalized = raw.trim();
      if (normalized.isBlank()) {
        return null;
      }

      for (Enum<?> constant : enumClass.getEnumConstants()) {
        if (constant.name().equalsIgnoreCase(normalized)) {
          return constant;
        }
      }

      throw new IllegalArgumentException(
          "Invalid ENUM value for field '" + fieldName + "': " + raw);
    }

    throw new IllegalArgumentException(
        "Invalid ENUM value type for field '" + fieldName + "': " + value);
  }
}
