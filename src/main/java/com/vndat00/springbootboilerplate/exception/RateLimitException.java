package com.vndat00.springbootboilerplate.exception;

public class RateLimitException extends RuntimeException {
  public RateLimitException(String message) {
    super(message);
  }
}
