package com.vndat00.springbootboilerplate.importexport.converter;

import java.util.UUID;

public final class UUIDImportConverter {

  private UUIDImportConverter() {}

  public static UUID toUUID(Object value, String fieldName) {
    if (value == null) {
      return null;
    }

    // UUID
    if (value instanceof UUID uuid) {
      return uuid;
    }

    // String
    if (value instanceof String raw) {
      String normalized = raw.trim();

      if (normalized.isBlank()) {
        return null;
      }

      try {
        return UUID.fromString(normalized);
      } catch (IllegalArgumentException ex) {
        throw new IllegalArgumentException(
            "Invalid UUID format for field '" + fieldName + "': " + raw, ex);
      }
    }

    // 3. byte[]
    if (value instanceof byte[] bytes && bytes.length == 16) {
      long msb = 0;
      long lsb = 0;
      for (int i = 0; i < 8; i++) {
        msb = (msb << 8) | (bytes[i] & 0xff);
      }
      for (int i = 8; i < 16; i++) {
        lsb = (lsb << 8) | (bytes[i] & 0xff);
      }
      return new UUID(msb, lsb);
    }

    throw new IllegalArgumentException(
        "Invalid UUID value type for field '"
            + fieldName
            + "': "
            + value
            + " ("
            + value.getClass().getName()
            + ")");
  }
}
