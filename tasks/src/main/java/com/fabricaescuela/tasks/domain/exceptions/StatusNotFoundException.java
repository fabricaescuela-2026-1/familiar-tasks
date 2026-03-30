package com.fabricaescuela.tasks.domain.exceptions;

public class StatusNotFoundException extends RuntimeException {
  public StatusNotFoundException(String status) {
    super("Status not found: " + status);
  }
}
