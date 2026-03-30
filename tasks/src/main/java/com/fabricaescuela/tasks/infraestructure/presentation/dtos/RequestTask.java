package com.fabricaescuela.tasks.infraestructure.presentation.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestTask {
  private String name;
  private String description;
  private String statusName;
  private String priorityName;
  private UUID homeId;
  private UUID guestId;
  private LocalDateTime deadline;
}
