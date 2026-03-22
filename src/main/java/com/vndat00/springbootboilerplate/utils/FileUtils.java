package com.vndat00.springbootboilerplate.utils;

import java.text.Normalizer;
import java.util.UUID;

public final class FileUtils {
  private FileUtils() {}

  public static String getFileExtension(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      return "";
    }

    String name =
        fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);

    int lastDotIndex = name.lastIndexOf('.');

    if (lastDotIndex <= 0 || lastDotIndex == name.length() - 1) {
      return "";
    }

    return name.substring(lastDotIndex + 1).toLowerCase();
  }

  public static String generateFileName(String originalFileName) {
    String extension = getFileExtension(originalFileName);
    String baseName = normalizeFileName(originalFileName);

    // UUID ensures uniqueness across uploads
    String uuid = UUID.randomUUID().toString().replace("-", "");

    // Limit base name length to keep object key short
    if (baseName.length() > 50) {
      baseName = baseName.substring(0, 50);
    }

    return extension.isEmpty() ? uuid + "_" + baseName : uuid + "_" + baseName + "." + extension;
  }

  private static String normalizeFileName(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      return "file";
    }

    String name =
        fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);

    int dotIndex = name.lastIndexOf('.');
    if (dotIndex > 0) {
      name = name.substring(0, dotIndex);
    }

    // Remove diacritics (e.g., Vietnamese accents)
    String normalized = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

    // Replace unsafe characters with "_"
    normalized = normalized.replaceAll("[^a-zA-Z0-9-_]", "_");

    // Collapse multiple underscores
    normalized = normalized.replaceAll("_+", "_");

    // Trim leading/trailing underscores
    normalized = normalized.replaceAll("^_+|_+$", "");

    return normalized.isBlank() ? "file" : normalized.toLowerCase();
  }
}
