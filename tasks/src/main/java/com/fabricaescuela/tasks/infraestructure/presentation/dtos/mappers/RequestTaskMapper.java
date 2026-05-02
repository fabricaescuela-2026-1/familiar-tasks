package com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers;

import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.RequestTask;

public class RequestTaskMapper {

  private RequestTaskMapper() {}

  public static Task toDomain(RequestTask request) {
    return Task.builder()
        .name(request.getName())
        .description(request.getDescription())
        .deadline(request.getDeadline())
        .homeId(request.getHomeId())
        .status(request.getStatusName())
        .priority(request.getPriorityName())
        .guestId(request.getGuestId())
        .build();
  }
}
