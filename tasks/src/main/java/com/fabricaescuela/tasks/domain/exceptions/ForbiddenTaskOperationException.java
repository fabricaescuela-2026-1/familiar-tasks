package com.fabricaescuela.tasks.domain.exceptions;

public class ForbiddenTaskOperationException extends RuntimeException {
  public ForbiddenTaskOperationException(String message) {
    super(message);
  }
}
