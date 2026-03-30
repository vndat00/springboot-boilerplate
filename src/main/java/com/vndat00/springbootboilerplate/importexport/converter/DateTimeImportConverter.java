package com.vndat00.springbootboilerplate.importexport.converter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeImportConverter {

  private static final List<DateTimeFormatter> LOCAL_DATE_TIME_FORMATTERS =
      List.of(
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
          DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
          DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
          DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
          DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

  private static final List<DateTimeFormatter> LOCAL_DATE_FORMATTERS =
      List.of(
          DateTimeFormatter.ISO_LOCAL_DATE,
          DateTimeFormatter.ofPattern("yyyy/MM/dd"),
          DateTimeFormatter.ofPattern("dd/MM/yyyy"));

  public static Timestamp toTimestamp(Object value, String fieldName) {
    if (value == null) {
      return null;
    }

    if (value instanceof Timestamp ts) {
      return ts;
    }

    if (value instanceof Date date) {
      return new Timestamp(date.getTime());
    }

    if (value instanceof LocalDateTime localDateTime) {
      return Timestamp.valueOf(localDateTime);
    }

    if (value instanceof LocalDate localDate) {
      return Timestamp.valueOf(localDate.atStartOfDay());
    }

    if (value instanceof Long epochMillis) {
      return new Timestamp(epochMillis);
    }

    if (value instanceof Integer epochSeconds) {
      return new Timestamp(epochSeconds.longValue() * 1000);
    }

    if (value instanceof String raw) {
      return parseStringToTimestamp(raw, fieldName);
    }

    throw new IllegalArgumentException(
        "Invalid DATE_TIME value for field '"
            + fieldName
            + "': "
            + value
            + " ("
            + value.getClass().getName()
            + ")");
  }

  private static Timestamp parseStringToTimestamp(String raw, String fieldName) {
    String value = raw == null ? null : raw.trim();
    if (value == null || value.isBlank()) {
      return null;
    }

    // Normalize ISO local datetime
    String normalized = value.replace("T", " ");

    // Try LocalDateTime with supported patterns
    for (DateTimeFormatter formatter : LOCAL_DATE_TIME_FORMATTERS) {
      try {
        LocalDateTime localDateTime = LocalDateTime.parse(normalized, formatter);
        return Timestamp.valueOf(localDateTime);
      } catch (DateTimeParseException ignored) {
      }
    }

    // Try full ISO local datetime directly
    try {
      LocalDateTime localDateTime =
          LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      return Timestamp.valueOf(localDateTime);
    } catch (DateTimeParseException ignored) {
    }

    // Try offset datetime, e.g. 2026-01-01T10:30:00+07:00
    try {
      OffsetDateTime offsetDateTime =
          OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      return Timestamp.from(offsetDateTime.toInstant());
    } catch (DateTimeParseException ignored) {
    }

    // Try LocalDate and assume start of day
    for (DateTimeFormatter formatter : LOCAL_DATE_FORMATTERS) {
      try {
        LocalDate localDate = LocalDate.parse(value, formatter);
        return Timestamp.valueOf(localDate.atStartOfDay());
      } catch (DateTimeParseException ignored) {
      }
    }

    // Try raw Timestamp.valueOf for safety
    try {
      return Timestamp.valueOf(normalized);
    } catch (IllegalArgumentException ignored) {
    }

    throw new IllegalArgumentException(
        "Invalid DATE_TIME format for field '" + fieldName + "': " + raw);
  }
}
