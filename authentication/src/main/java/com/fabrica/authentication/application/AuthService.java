package com.fabrica.authentication.application;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.application.ports.in.AuthUseCase;
import com.fabrica.authentication.application.ports.out.UserQueuePort;
import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InactiveAccountException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
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
    if (
      req.passwordConfirmation() != null &&
      !req.password().equals(req.passwordConfirmation())
    ) {
      throw new IllegalArgumentException(
        "Password and confirmation do not match"
      );
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
      .isActive(false)
      .build();

    userRepo.save(user);
    Token accessToken = jwtService.generateAccesToken(user);
    Token refreshToken = jwtService.generateRefreshToken(user);
    tokenRepo.save(accessToken);
    tokenRepo.save(refreshToken);

    log.info("Registration complete");
    userQueuePort.sendUserMessage(user);
    return new AuthResponse(
      accessToken.getTokenHash(),
      refreshToken.getTokenHash()
    );
  }

  @Override
  public AuthResponse login(LoginRequest request) {
    var user = userRepo
      .findByEmail(request.email())
      .orElseThrow(() -> new UserNotFoundException());

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new UserNotFoundException("Invalid credentials");
    }
    log.info("El usuario esta activo: {}", user.isActive());
    if (!user.isActive()) {
      throw new InactiveAccountException();
    }

    Token accessToken = jwtService.generateAccesToken(user);
    Token refreshToken = jwtService.generateRefreshToken(user);

    tokenRepo.save(accessToken);
    tokenRepo.save(refreshToken);
    return new AuthResponse(
      accessToken.getTokenHash(),
      refreshToken.getTokenHash()
    );
  }

  @Override
  public AuthResponse refreshToken(String refreshToken) {
    var token = tokenRepo
      .findByHash(refreshToken)
      .orElseThrow(InvalidRefreshTokenException::new);

    if (!jwtService.isTokenValid(token)) {
      throw new InvalidRefreshTokenException();
    }

    Token newAccessToken = jwtService.generateAccesToken(token.getUser());
    tokenRepo.save(newAccessToken);
    return new AuthResponse(
      newAccessToken.getTokenHash(),
      token.getTokenHash()
    );
  }

  private void validatePasswordComplexity(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
    if (password.length() < 8) {
      throw new IllegalArgumentException(
        "Password must be at least 8 characters"
      );
    }
    boolean hasUpper = false;
    boolean hasLower = false;
    boolean hasDigit = false;
    boolean hasSpecial = false;
    for (int i = 0; i < password.length(); i++) {
      char c = password.charAt(i);
      if (Character.isUpperCase(c)) hasUpper = true;
      else if (Character.isLowerCase(c)) hasLower = true;
      else if (Character.isDigit(c)) hasDigit = true;
      else hasSpecial = true;
    }
    if (!hasUpper) throw new IllegalArgumentException(
      "Password must contain at least one uppercase letter"
    );
    if (!hasLower) throw new IllegalArgumentException(
      "Password must contain at least one lowercase letter"
    );
    if (!hasDigit) throw new IllegalArgumentException(
      "Password must contain at least one digit"
    );
    if (!hasSpecial) throw new IllegalArgumentException(
      "Password must contain at least one special character"
    );
  }

  @Override
  public TokenResponse getToken(String tokenHash) {
    Token token = tokenRepo
      .findByHash(tokenHash)
      .orElseThrow(InvalidTokenException::new);

    if (
      token.getExpirationDate() != null &&
      token.getExpirationDate().isBefore(LocalDateTime.now())
    ) {
      throw new InvalidTokenException();
    }
    if (
      token.getExpiratedAt() != null &&
      token.getExpiratedAt().isBefore(LocalDateTime.now())
    ) {
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
