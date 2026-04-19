package com.fabrica.authentication.domain.ports.out;

import com.fabrica.authentication.domain.model.Token;

public interface JwtServicePort {
  Token generateAccesToken(String email);

  Token generateRefreshToken(String email);

  boolean isTokenValid(Token token);
}
