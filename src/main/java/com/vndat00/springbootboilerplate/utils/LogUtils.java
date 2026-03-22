package com.vndat00.springbootboilerplate.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LogUtils {

  private LogUtils() {}

  /**
   * Extract log message
   *
   * @param method Error method
   * @param uri Error uri
   * @param error Error message
   */
  public static void error(String method, String uri, String error) {
    String message = method + "/" + uri + " - " + "Error: " + error;
    log.error(message);
//    Sentry.captureException(new Exception(message));
  }

  public static void error(String message) {
    log.error(message);
//    Sentry.captureException(new Exception(message));
  }

  public static void warn(String message) {
    log.warn(message);
  }

  public static void warn(String message, Object... args) {
    log.warn(message, args);
  }

  public static void info(String message) {
    log.info(message);
  }

  public static void info(String message, Object... args) {
    log.info(message, args);
  }
}
