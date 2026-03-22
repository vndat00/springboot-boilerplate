package com.vndat00.springbootboilerplate.exception;

public class InternalServerException extends RuntimeException {

  public InternalServerException(String message) {
    super(message);
  }
}
