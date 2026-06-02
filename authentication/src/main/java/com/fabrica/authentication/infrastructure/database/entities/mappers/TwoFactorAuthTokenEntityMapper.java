package com.fabrica.authentication.infrastructure.database.entities.mappers;

import com.fabrica.authentication.domain.model.TwoFactorAuthToken;
import com.fabrica.authentication.infrastructure.database.entities.TwoFactorAuthTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TwoFactorAuthTokenEntityMapper {

  private final UserEntityMapper userEntityMapper;

  public TwoFactorAuthTokenEntity toEntity(TwoFactorAuthToken token) {
    return TwoFactorAuthTokenEntity.builder()
      .id(token.getId())
      .user(userEntityMapper.toEntity(token.getUser()))
      .codeHash(token.getCodeHash())
      .createdAt(token.getCreatedAt())
      .expiresAt(token.getExpiresAt())
      .attempts(token.getAttempts())
      .invalidated(token.isInvalidated())
      .build();
  }

  public TwoFactorAuthToken toDomain(TwoFactorAuthTokenEntity entity) {
    return TwoFactorAuthToken.builder()
      .user(userEntityMapper.toDomain(entity.getUser()))
      .codeHash(entity.getCodeHash())
      .createdAt(entity.getCreatedAt())
      .expiresAt(entity.getExpiresAt())
      .attempts(entity.getAttempts())
      .invalidated(entity.isInvalidated())
      .build();
  }
}
