package com.fabrica.authentication.domain.ports.out;

import com.fabrica.authentication.domain.model.Token;
import java.util.Optional;

public interface TokenRepositoryPort {
  Token save(Token token);

  void revokeAllByUserEmail(String email);

  Optional<Token> findByHash(String tokenHash);
}
