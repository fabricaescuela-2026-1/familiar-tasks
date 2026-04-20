package com.fabrica.authentication.domain.exceptions;

public class EmailAlreadyExitsException extends RuntimeException {
  public EmailAlreadyExitsException(String email) {
    super("Email already exists: " + email);
  }

}
