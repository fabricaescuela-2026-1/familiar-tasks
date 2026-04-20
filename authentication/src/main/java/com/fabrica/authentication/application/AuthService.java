package com.fabrica.authentication.application;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.ports.in.AuthUseCase;
import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService implements AuthUseCase {
  private final JwtServicePort jwtService;
  private final TokenRepositoryPort tokenRepo;
  private final UserRepositoryPort userRepo;
  private final PasswordEncoder passwordEncoder;

  @Override
  public AuthResponse register(RegisterRequest req) {
    // TODO: validate request

    if (userRepo.findByEmail(req.email()).isPresent()) {
      throw new EmailAlreadyExitsException(req.email());
    }
    var user = User.builder()
        .name(req.name())
        .lastname(req.lastname())
        .email(req.email())
        .passwordHash(passwordEncoder.encode(req.password()))
        .createdAt(LocalDateTime.now())
        .isActive(true)
        .build();

    userRepo.save(user);
    Token accessToken = jwtService.generateAccesToken(user);
    Token refreshToken = jwtService.generateRefreshToken(user);
    tokenRepo.save(accessToken);
    tokenRepo.save(refreshToken);
    return new AuthResponse(accessToken.getTokenHash(), refreshToken.getTokenHash());
  }

  @Override
  public AuthResponse login(LoginRequest request) {
    // TODO: validate login request

    var user = userRepo.findByEmail(request.email())
        .orElseThrow(() -> new UserNotFoundException("User email " + request.email() + " not found"));

    Token accessToken = jwtService.generateAccesToken(user);
    Token refreshToken = jwtService.generateRefreshToken(user);
    tokenRepo.save(accessToken);
    tokenRepo.save(refreshToken);
    return new AuthResponse(accessToken.getTokenHash(), refreshToken.getTokenHash());
  }

  @Override
  public AuthResponse refreshToken(String refreshToken) {
    var token = tokenRepo.findByHash(refreshToken)
        .orElseThrow(() -> new InvalidRefreshTokenException());

    if (!jwtService.isTokenValid(token)) {
      throw new InvalidRefreshTokenException();
    }

    // Generate new access token
    Token newAccessToken = jwtService.generateAccesToken(token.getUser());
    tokenRepo.save(newAccessToken);
    return new AuthResponse(newAccessToken.getTokenHash(), token.getTokenHash());
  }

}
