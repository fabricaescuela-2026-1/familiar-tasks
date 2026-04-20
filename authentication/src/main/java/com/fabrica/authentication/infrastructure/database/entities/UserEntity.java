package com.fabrica.authentication.infrastructure.database.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "auth")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserEntity {

  @Column(name = "user_id")
  @Id
  private UUID userId;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(length = 50, nullable = false)
  private String lastname;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Column(length = 255, nullable = false, unique = true)
  private String email;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}