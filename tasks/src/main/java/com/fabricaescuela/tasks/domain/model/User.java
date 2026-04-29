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
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {
  private UUID userId;
  private String name;
  private String lastname;
  private String email;
  private String passwordHash;
  private boolean isActive;
  private LocalDateTime createdAt;
}

