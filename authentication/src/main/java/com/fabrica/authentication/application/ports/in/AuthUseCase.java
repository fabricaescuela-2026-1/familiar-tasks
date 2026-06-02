package com.fabrica.authentication.application.ports.in;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;

public interface AuthUseCase {
  void register(RegisterRequest request);

  void login(LoginRequest request);

  AuthResponse verifyTwoFactorAuthCode(String code, String email);

  AuthResponse refreshToken(String refreshToken);

  TokenResponse getToken(String tokenHash);
}
