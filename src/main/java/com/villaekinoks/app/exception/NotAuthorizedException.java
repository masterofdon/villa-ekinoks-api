package com.villaekinoks.app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotAuthorizedException extends RuntimeException {

  private static final long serialVersionUID = 1198481999;

  private final Integer code = HttpStatus.UNAUTHORIZED.value();

  private final String responsecode;

  public NotAuthorizedException() {
    super();
    responsecode = null;
  }

  public NotAuthorizedException(String message) {
    super(message);
    responsecode = null;
  }

  public NotAuthorizedException(String message, String responsecode) {
    super(message);
    this.responsecode = responsecode;
  }
}
