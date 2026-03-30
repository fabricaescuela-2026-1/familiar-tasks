package com.fabricaescuela.tasks.domain.model;

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
public class Task {
  private UUID taskId;
  private String name;
  private String description;
  private String status;
  private String priority;
  private UUID homeId;
  private UUID guestId;
  private LocalDateTime createdAt;
  private LocalDateTime deadline;
}
