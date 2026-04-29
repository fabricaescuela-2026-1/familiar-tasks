package com.fabrica.authentication.application.ports.in;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;

public interface AuthUseCase {
  AuthResponse register(RegisterRequest request);

  AuthResponse login(LoginRequest request);

  AuthResponse refreshToken(String refreshToken);

  TokenResponse getToken(String tokenHash);
}
