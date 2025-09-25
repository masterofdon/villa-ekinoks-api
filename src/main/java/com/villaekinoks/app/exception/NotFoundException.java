package com.villaekinoks.app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1198481999;

  private final Integer code = HttpStatus.NOT_FOUND.value();

  private final String responsecode;

  public NotFoundException() {
    super();
    responsecode = null;
  }

  public NotFoundException(String message) {
    super(message);
    responsecode = null;
  }

  public NotFoundException(String message, String responsecode) {
    super(message);
    this.responsecode = responsecode;
  }
}