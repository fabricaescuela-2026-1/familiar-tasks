package com.fabrica.authentication.domain.exceptions;

public class InvalidTowFactorAuthTokenException extends RuntimeException {

  public InvalidTowFactorAuthTokenException(String message) {
    super(message);
  }
}
