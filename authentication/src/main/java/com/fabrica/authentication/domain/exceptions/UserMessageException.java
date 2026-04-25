package com.fabrica.authentication.domain.exceptions;

public class UserMessageException extends RuntimeException {
  public UserMessageException(String message) {
    super("Error en envio de menesaje: " + message);
  }
}
