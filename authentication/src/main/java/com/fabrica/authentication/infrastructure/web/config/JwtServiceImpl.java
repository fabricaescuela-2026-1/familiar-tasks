package com.fabrica.authentication.infrastructure.web.config;

import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;

public class JwtServiceImpl implements JwtServicePort {

  @Override
  public Token generateAccesToken(String email) {
    return null;
  }

  @Override
  public Token generateRefreshToken(String email) {
    return null;
  }

  @Override
  public boolean isTokenValid(Token token) {
    return false;
  }

}
