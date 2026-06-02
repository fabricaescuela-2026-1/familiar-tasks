package com.fabrica.authentication.domain.exceptions;

public class EmailSendingException extends RuntimeException {

  public EmailSendingException(String message) {
    super(message);
  }
}
