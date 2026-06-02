package com.fabrica.authentication.domain.exceptions;

public class InvalidTwoFactorAuthTokenException extends RuntimeException {

  public InvalidTwoFactorAuthTokenException(String message) {
    super(message);
  }
}
