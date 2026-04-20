package com.fabrica.authentication.infrastructure.database.entities.mappers;

import org.springframework.stereotype.Component;

import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.infrastructure.database.entities.TokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.TokenType;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenEntityMapper {
  private final UserJpaRepository userJpaRepo;
  private final UserEntityMapper userEntityMapper;

  public TokenEntity toEntity(Token token) {
    UserEntity userEntity = userJpaRepo.findByEmail(token.getUser().getEmail())
        .orElseThrow(() -> new UserNotFoundException("User not found"));
    return TokenEntity.builder()
        .tokenId(token.getTokenId())
        .tokenHash(token.getTokenHash())
        .expirationDate(token.getExpirationDate())
        .expiratedAt(token.getExpiratedAt())
        .tokenType(TokenType.valueOf(token.getTokenType()))
        .user(userEntity)
        .build();
  }

  public Token toDomain(TokenEntity tokenEntity) {
    return Token.builder()
        .tokenId(tokenEntity.getTokenId())
        .tokenHash(tokenEntity.getTokenHash())
        .expirationDate(tokenEntity.getExpirationDate())
        .expiratedAt(tokenEntity.getExpiratedAt())
        .tokenType(tokenEntity.getTokenType().name())
        .user(userEntityMapper.toDomain(tokenEntity.getUser()))
        .build();
  }
}
