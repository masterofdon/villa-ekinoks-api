package com.villaekinoks.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.villaekinoks.app.generic.api.GenericApiResponse;

@ControllerAdvice
public class RestAdvice {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<GenericApiResponse<Void>> handleException(NotFoundException ex) {
    return new ResponseEntity<>(new GenericApiResponse<>(
        HttpStatus.UNAUTHORIZED.value(),
        ex.getMessage(),
        ex.getResponsecode()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NotAuthorizedException.class)
  public ResponseEntity<GenericApiResponse<Void>> handleException(NotAuthorizedException ex) {
    return new ResponseEntity<>(
        new GenericApiResponse<>(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            ex.getResponsecode()),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadApiRequestException.class)
  public ResponseEntity<GenericApiResponse<Void>> handleException(BadApiRequestException ex) {
    return new ResponseEntity<>(
        new GenericApiResponse<>(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            ex.getResponsecode()),
        HttpStatus.BAD_REQUEST);
  }
}
