package com.vndat00.springbootboilerplate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Getter
public class UnprocessableRequestException extends RuntimeException {
  private final String detail;

  public UnprocessableRequestException(String message) {
    this(message, null);
  }

  public UnprocessableRequestException(String message, String detail) {
    super(message);
    this.detail = detail;
  }
}
