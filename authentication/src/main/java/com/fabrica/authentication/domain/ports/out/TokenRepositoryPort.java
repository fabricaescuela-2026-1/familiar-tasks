package com.fabrica.authentication.domain.ports.out;

import java.util.Optional;

import com.fabrica.authentication.domain.model.Token;

public interface TokenRepositoryPort {
  Token save(Token token);

  void revokeAllByUserEmail(String email);

  Optional<Token> findByHash(String tokenHash);
}
