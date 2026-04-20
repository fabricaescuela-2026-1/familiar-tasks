package com.fabrica.authentication.infrastructure.database;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.mappers.TokenEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.TokenJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepositoryPort {
  private final TokenJpaRepository tokenJpaRepo;
  private final TokenEntityMapper tokenEntityMapper;

  @Override
  public Token save(Token token) {
    return tokenEntityMapper.toDomain(tokenJpaRepo.save(tokenEntityMapper.toEntity(token)));
  }

  @Override
  public void revokeAllByUserEmail(String email) {
    tokenJpaRepo.revokeAllByUserEmail(email);
  }

  @Override
  public Optional<Token> findByHash(String tokenHash) {
    return tokenJpaRepo.findByTokenHash(tokenHash).map(tokenEntityMapper::toDomain);
  }
}
