package com.fabrica.authentication.domain.exceptions;

public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException() {
    super("Token invalido");
  }
}
