package com.fabrica.authentication.infrastructure.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "activation_tokens", schema = "auth")
@Data
@Builder
public class ActivationTokenEntity {

  @Id
  private UUID id;

  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @JoinColumn(name = "code_hash", nullable = false)
  private String codeHash;

  @Column(
    name = "created_at",
    nullable = false,
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
  )
  private LocalDateTime createdAt;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(
    name = "attempts",
    nullable = false,
    columnDefinition = "INT DEFAULT 0"
  )
  private int attempts;

  @Column(
    name = "invalidated",
    nullable = false,
    columnDefinition = "BOOLEAN DEFAULT FALSE"
  )
  private boolean invalidated;
}
