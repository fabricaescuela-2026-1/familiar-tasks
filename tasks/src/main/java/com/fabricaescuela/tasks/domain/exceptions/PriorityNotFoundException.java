package com.fabricaescuela.tasks.domain.exceptions;

public class PriorityNotFoundException extends RuntimeException {
  public PriorityNotFoundException(String priority) {
    super("Priority not found: " + priority);
  }
}
