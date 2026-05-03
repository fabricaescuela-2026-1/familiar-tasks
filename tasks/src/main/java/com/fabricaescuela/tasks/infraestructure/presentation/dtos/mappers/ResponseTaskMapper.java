package com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers;

import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ResponseTask;

public class ResponseTaskMapper {

  private ResponseTaskMapper() {}

  public static ResponseTask toResponse(Task task) {
    return ResponseTask.builder()
        .taskId(task.getTaskId())
        .name(task.getName())
        .description(task.getDescription())
        .statusName(task.getStatus())
        .priorityName(task.getPriority())
        .homeId(task.getHomeId())
        .createdAt(task.getCreatedAt())
        .deadline(task.getDeadline())
        .guestId(task.getGuestId())
        .build();
  }
}
