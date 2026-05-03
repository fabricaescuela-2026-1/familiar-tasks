package com.fabricaescuela.tasks.infraestructure.database.mappers;

import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;

public class TaskEntityMapper {

  private TaskEntityMapper() {}

  public static Task toDomain(TaskEntity entity) {
    return Task.builder()
        .taskId(entity.getTaskId())
        .name(entity.getName())
        .description(entity.getDescription())
        .deadline(entity.getDeadline())
        .createdAt(entity.getCreatedAt())
        .status(entity.getStatus().getName())
        .priority(entity.getPriority().getName())
        .homeId(entity.getHomeId())
        .guestId(entity.getGuestId())
        .build();
  }

  public static TaskEntity toEntity(Task task) {
    return TaskEntity.builder()
        .taskId(task.getTaskId())
        .name(task.getName())
        .description(task.getDescription())
        .deadline(task.getDeadline())
        .createdAt(task.getCreatedAt())
        .homeId(task.getHomeId())
        .guestId(task.getGuestId())
        .build();
  }
}
