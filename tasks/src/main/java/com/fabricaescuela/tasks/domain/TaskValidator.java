package com.fabricaescuela.tasks.domain;

import java.time.LocalDateTime;

import com.fabricaescuela.tasks.domain.model.Task;

public class TaskValidator {

  private TaskValidator() {}

  public static void validate(Task task) {
    if (task.getName() == null || task.getName().isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }
    if (task.getDescription() == null || task.getDescription().isBlank()) {
      throw new IllegalArgumentException("Description is required");
    }
    if (task.getStatus() == null || task.getStatus().isBlank()) {
      throw new IllegalArgumentException("Status is required");
    }
    if (task.getPriority() == null || task.getPriority().isBlank()) {
      throw new IllegalArgumentException("Priority is required");
    }
    if (task.getDeadline() == null) {
      throw new IllegalArgumentException("Deadline is required");
    }
    if (task.getDeadline().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Deadline must be in the future");
    }
    if (task.getHomeId() == null) {
      throw new IllegalArgumentException("HomeId is required");
    }

  }
  public static void validateUserIds(Task task) {
    if (task.getGuestId() == null) {
      throw new IllegalArgumentException("GuestId is required");
    }
  }
}
