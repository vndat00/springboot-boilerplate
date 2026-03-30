package com.vndat00.springbootboilerplate.importexport.converter;

import java.math.BigDecimal;

public final class NumberImportConverter {

  private NumberImportConverter() {}

  public static Integer toInteger(Object value, String fieldName) {
    if (value == null) {
      return null;
    }

    if (value instanceof Integer i) {
      return i;
    }

    if (value instanceof Number n) {
      return n.intValue();
    }

    if (value instanceof String raw) {
      String normalized = raw.trim();
      if (normalized.isBlank()) {
        return null;
      }
      try {
        return Integer.valueOf(normalized);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException(
            "Invalid INTEGER value for field '" + fieldName + "': " + raw, ex);
      }
    }

    throw new IllegalArgumentException(
        "Invalid INTEGER value type for field '" + fieldName + "': " + value);
  }

  public static BigDecimal toBigDecimal(Object value, String fieldName) {
    if (value == null) {
      return null;
    }

    if (value instanceof BigDecimal bd) {
      return bd;
    }

    if (value instanceof Number n) {
      return BigDecimal.valueOf(n.doubleValue());
    }

    if (value instanceof String raw) {
      String normalized = raw.trim();
      if (normalized.isBlank()) {
        return null;
      }
      try {
        return new BigDecimal(normalized);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException(
            "Invalid DECIMAL value for field '" + fieldName + "': " + raw, ex);
      }
    }

    throw new IllegalArgumentException(
        "Invalid DECIMAL value type for field '" + fieldName + "': " + value);
  }
}
