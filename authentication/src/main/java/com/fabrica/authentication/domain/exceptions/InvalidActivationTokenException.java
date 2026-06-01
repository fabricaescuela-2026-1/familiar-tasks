package com.fabrica.authentication.domain.exceptions;

public class InvalidActivationTokenException extends RuntimeException {

  public InvalidActivationTokenException(String message) {
    super(message);
  }
}
