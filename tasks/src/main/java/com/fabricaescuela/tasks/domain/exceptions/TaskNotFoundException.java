package com.fabricaescuela.tasks.domain.exceptions;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
  public TaskNotFoundException(UUID taskId) {
    super("Task not found: " + taskId);
  }
}
