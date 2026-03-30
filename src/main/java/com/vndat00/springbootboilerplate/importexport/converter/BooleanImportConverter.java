package com.vndat00.springbootboilerplate.importexport.converter;

import java.util.Set;

public final class BooleanImportConverter {

  private static final Set<String> TRUE_VALUES = Set.of("true", "1", "yes", "y");
  private static final Set<String> FALSE_VALUES = Set.of("false", "0", "no", "n");

  private BooleanImportConverter() {}

  public static Boolean toBoolean(Object value, String fieldName) {
    if (value == null) {
      return null;
    }

    if (value instanceof Boolean b) {
      return b;
    }

    if (value instanceof Number n) {
      int number = n.intValue();
      if (number == 1) {
        return true;
      }
      if (number == 0) {
        return false;
      }
    }

    if (value instanceof String raw) {
      String normalized = raw.trim().toLowerCase();
      if (normalized.isBlank()) {
        return null;
      }
      if (TRUE_VALUES.contains(normalized)) {
        return true;
      }
      if (FALSE_VALUES.contains(normalized)) {
        return false;
      }
    }

    throw new IllegalArgumentException(
        "Invalid BOOLEAN value for field '" + fieldName + "': " + value);
  }
}
