package com.fabrica.authentication.infrastructure.database.entities.mappers;

import com.fabrica.authentication.domain.model.ActivationToken;
import com.fabrica.authentication.infrastructure.database.entities.ActivationTokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivationTokenMapper {

  public ActivationTokenEntity mapToEntity(
    ActivationToken token,
    UserEntity userEntity
  ) {
    var now = LocalDateTime.now();
    return ActivationTokenEntity.builder()
      .user(userEntity)
      .codeHash(token.getCodeHash())
      .createdAt(now)
      .expiresAt(now.plusMinutes(15))
      .attempts(0)
      .invalidated(false)
      .build();
  }

  public ActivationToken mapToActivationToken(
    ActivationTokenEntity tokenEntity
  ) {
    return ActivationToken.builder()
      .id(tokenEntity.getId())
      .codeHash(tokenEntity.getCodeHash())
      .createdAt(tokenEntity.getCreatedAt())
      .expiresAt(tokenEntity.getExpiresAt())
      .attempts(tokenEntity.getAttempts())
      .invalidated(tokenEntity.isInvalidated())
      .build();
  }
}
