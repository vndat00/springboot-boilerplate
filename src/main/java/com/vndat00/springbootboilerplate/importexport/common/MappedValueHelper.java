package com.vndat00.springbootboilerplate.importexport.common;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;

public final class MappedValueHelper {
  private MappedValueHelper() {}

  public static String getString(Object value) {
    return value == null ? null : value.toString();
  }

  public static Integer getInteger(Object value, String key) {
    if (value == null) {
      return null;
    }
    if (value instanceof Integer i) {
      return i;
    }
    if (value instanceof Number n) {
      return n.intValue();
    }
    if (value instanceof String s) {
      try {
        return Integer.valueOf(s.trim());
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid integer for " + key + ": " + value, ex);
      }
    }
    throw new IllegalArgumentException("Invalid integer for " + key + ": " + value);
  }

  public static BigDecimal getBigDecimal(Object value, String key) {
    if (value == null) {
      return null;
    }
    if (value instanceof BigDecimal bd) {
      return bd;
    }
    if (value instanceof Number n) {
      return BigDecimal.valueOf(n.doubleValue());
    }
    if (value instanceof String s) {
      try {
        return new BigDecimal(s.trim());
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid decimal for " + key + ": " + value, ex);
      }
    }
    throw new IllegalArgumentException("Invalid decimal for " + key + ": " + value);
  }

  public static Boolean getBoolean(Object value, String key) {
    if (value == null) {
      return null;
    }
    if (value instanceof Boolean b) {
      return b;
    }
    if (value instanceof String s) {
      String normalized = s.trim().toLowerCase(Locale.ROOT);
      if ("true".equals(normalized) || "1".equals(normalized) || "yes".equals(normalized)) {
        return true;
      }
      if ("false".equals(normalized) || "0".equals(normalized) || "no".equals(normalized)) {
        return false;
      }
      throw new IllegalArgumentException("Invalid boolean for " + key + ": " + value);
    }
    throw new IllegalArgumentException("Invalid boolean for " + key + ": " + value);
  }

  public static Timestamp getTimestamp(Object value, String key) {
    if (value == null) {
      return null;
    }
    if (value instanceof Timestamp ts) {
      return ts;
    }
    if (value instanceof String s) {
      String normalized = s.trim().replace("T", " ");
      try {
        return Timestamp.valueOf(normalized);
      } catch (IllegalArgumentException ex) {
        throw new IllegalArgumentException("Invalid timestamp for " + key + ": " + value, ex);
      }
    }
    throw new IllegalArgumentException("Invalid timestamp for " + key + ": " + value);
  }

  public static <E extends Enum<E>> E getEnum(Object value, String key, Class<E> clazz) {
    if (value == null) {
      return null;
    }
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    if (value instanceof String s) {
      try {
        return Enum.valueOf(clazz, s.trim().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException ex) {
        throw new IllegalArgumentException("Invalid enum for " + key + ": " + value, ex);
      }
    }
    throw new IllegalArgumentException("Invalid enum for " + key + ": " + value);
  }
}
