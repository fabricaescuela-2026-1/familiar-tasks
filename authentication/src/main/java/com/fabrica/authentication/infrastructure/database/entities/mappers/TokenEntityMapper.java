package com.fabrica.authentication.infrastructure.database.entities.mappers;

import org.springframework.stereotype.Component;

import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.infrastructure.database.entities.TokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.TokenType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenEntityMapper {

  private final UserEntityMapper userEntityMapper;

  public TokenEntity tokenEntity(Token token) {
    return TokenEntity.builder()
        .tokenId(token.getTokenId())
        .tokenHash(token.getTokenHash())
        .expirationDate(token.getExpirationDate())
        .expiratedAt(token.getExpiratedAt())
        .tokenType(TokenType.valueOf(token.getTokenType()))
        .user(userEntityMapper.toEntity(token.getUser()))
        .build();
  }
}
