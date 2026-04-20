package com.fabrica.authentication.domain.ports.out;

import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;

public interface JwtServicePort {
  Token generateAccesToken(User user);

  Token generateRefreshToken(User user);

  boolean isTokenValid(Token token);
}
