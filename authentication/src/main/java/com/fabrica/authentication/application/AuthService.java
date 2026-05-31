package com.fabrica.authentication.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.application.ports.in.AuthUseCase;
import com.fabrica.authentication.application.ports.out.UserQueuePort;
import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@RequiredArgsConstructor
@Service
@Log
@Transactional
public class AuthService implements AuthUseCase {
  private final JwtServicePort jwtService;
  private final TokenRepositoryPort tokenRepo;
  private final UserRepositoryPort userRepo;
  private final PasswordEncoder passwordEncoder;
  private final UserQueuePort userQueuePort;

  @Override
  public AuthResponse register(RegisterRequest req) {
    if (req.name() == null || req.name().isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }
    if (req.lastname() == null || req.lastname().isBlank()) {
      throw new IllegalArgumentException("Lastname is required");
    }
    if (req.email() == null || req.email().isBlank()) {
      throw new IllegalArgumentException("Email is required");
    }
    validatePasswordComplexity(req.password());
    if (req.passwordConfirmation() != null && !req.password().equals(req.passwordConfirmation())) {
      throw new IllegalArgumentException("Password and confirmation do not match");
    }

    if (userRepo.findByEmail(req.email()).isPresent()) {
      throw new EmailAlreadyExitsException(req.email());
    }
    var user = User.builder()
        .name(req.name())
        .userId(UUID.randomUUID())
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

    log.info("Registration complete");
    userQueuePort.sendUserMessage(user);
    return new AuthResponse(accessToken.getTokenHash(), refreshToken.getTokenHash());
  }

  @Override
  public AuthResponse login(LoginRequest request) {
    // TODO: validate login request

    var user = userRepo.findByEmail(request.email())
        .orElseThrow(() -> new UserNotFoundException("User email " + request.email() + " not found"));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new UserNotFoundException("Invalid credentials");
    }

    Token accessToken = jwtService.generateAccesToken(user);
    Token refreshToken = jwtService.generateRefreshToken(user);

    tokenRepo.save(accessToken);
    tokenRepo.save(refreshToken);
    return new AuthResponse(accessToken.getTokenHash(), refreshToken.getTokenHash());
  }

  @Override
  public AuthResponse refreshToken(String refreshToken) {
    var token = tokenRepo.findByHash(refreshToken)
        .orElseThrow(InvalidRefreshTokenException::new);

    if (!jwtService.isTokenValid(token)) {
      throw new InvalidRefreshTokenException();
    }

    Token newAccessToken = jwtService.generateAccesToken(token.getUser());
    tokenRepo.save(newAccessToken);
    return new AuthResponse(newAccessToken.getTokenHash(), token.getTokenHash());
  }

  private void validatePasswordComplexity(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
    if (password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters");
    }
    if (password.chars().noneMatch(Character::isUpperCase)) {
      throw new IllegalArgumentException("Password must contain at least one uppercase letter");
    }
    if (password.chars().noneMatch(Character::isLowerCase)) {
      throw new IllegalArgumentException("Password must contain at least one lowercase letter");
    }
    if (password.chars().noneMatch(Character::isDigit)) {
      throw new IllegalArgumentException("Password must contain at least one digit");
    }
    if (password.chars().allMatch(c -> Character.isUpperCase(c) || Character.isLowerCase(c) || Character.isDigit(c))) {
      throw new IllegalArgumentException("Password must contain at least one special character");
    }
  }

  @Override
  public TokenResponse getToken(String tokenHash) {
    Token token = tokenRepo.findByHash(tokenHash)
        .orElseThrow(InvalidTokenException::new);

    if (token.getExpirationDate() != null && token.getExpirationDate().isBefore(LocalDateTime.now())) {
      throw new InvalidTokenException();
    }
    if (token.getExpiratedAt() != null && token.getExpiratedAt().isBefore(LocalDateTime.now())) {
      throw new InvalidTokenException();
    }

    return TokenResponse.builder()
        .tokenId(token.getTokenId())
        .tokenHash(token.getTokenHash())
        .expirationDate(token.getExpirationDate())
        .userId(token.getUser().getUserId())
        .tokenType(token.getTokenType())
        .expiratedAt(token.getExpiratedAt())
        .build();
  }

}
