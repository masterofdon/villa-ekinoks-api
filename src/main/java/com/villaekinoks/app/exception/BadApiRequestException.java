package com.villaekinoks.app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BadApiRequestException extends RuntimeException {

  private static final long serialVersionUID = 1198481999;

  private final Integer code = HttpStatus.BAD_REQUEST.value();

  private final String responsecode;

  public BadApiRequestException() {
    super();
    responsecode = null;
  }

  public BadApiRequestException(String message) {
    super(message);
    responsecode = null;

  }

  public BadApiRequestException(String message, String responsecode) {
    super(message);
    this.responsecode = responsecode;
  }
}