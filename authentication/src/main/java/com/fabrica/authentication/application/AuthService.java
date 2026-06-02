package com.fabrica.authentication.application;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.application.dto.mail.EmailProperties;
import com.fabrica.authentication.application.ports.in.AuthUseCase;
import com.fabrica.authentication.application.ports.out.EmailSendingPort;
import com.fabrica.authentication.application.ports.out.UserQueuePort;
import com.fabrica.authentication.application.util.CodeGeneration;
import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InactiveAccountException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTwoFactorAuthTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.TwoFactorAuthToken;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.TwoFactorAuthTokenRepositoryPort;
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
  private final EmailSendingPort emailSendingComp;
  private final TwoFactorAuthTokenRepositoryPort twoFactorAuthTokenRepo;

  @Override
  public void register(RegisterRequest req) {
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

    boolean existsUserWithEmail = userRepo.findByEmail(req.email()).isPresent();
    if (existsUserWithEmail) {
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

    log.info("Registration complete");
    userQueuePort.sendUserMessage(user);
  }

  @Override
  @Transactional
  public void login(LoginRequest request) {
    var user = getUserByEmail(request.email());

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new UserNotFoundException("Invalid credentials");
    }
    log.info("El usuario esta activo: {}", user.isActive());
    if (!user.isActive()) {
      throw new InactiveAccountException();
    }

    twoFactorAuthTokenRepo.invalidateAllByUserEmail(request.email());

    var code = CodeGeneration.generateSixDigitCode();
    var codeHash = passwordEncoder.encode(code);

    var emailProps = EmailProperties.builder()
      .code(code)
      .recipient(user.getEmail())
      .subject("Codigo de verificacion de dos factores")
      .build();

    var twoFactorAuthToken = createTwoFactorAuthToken(user, codeHash);
    twoFactorAuthTokenRepo.save(twoFactorAuthToken);

    emailSendingComp.sendCodeEmail(emailProps);
  }

  private TwoFactorAuthToken createTwoFactorAuthToken(
    User user,
    String codeHash
  ) {
    var now = LocalDateTime.now();
    return TwoFactorAuthToken.builder()
      .id(UUID.randomUUID())
      .createdAt(now)
      .expiresAt(now.plusMinutes(10))
      .invalidated(false)
      .attempts(0)
      .codeHash(codeHash)
      .user(user)
      .build();
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

  @Override
  @Transactional
  public AuthResponse verifyTwoFactorAuthCode(String code, String email) {
    log.info("Verifying two-factor auth code for user: {}", email);
    var user = getUserByEmail(email);
    var authToken = getTwoFactorAuthTokenByEmail(email);

    twoFactorAuthTokenRepo.increaseAttemptsByOne(authToken.getId());

    if (!passwordEncoder.matches(code, authToken.getCodeHash())) {
      throw new InvalidTwoFactorAuthTokenException("Codigo incorrecto");
    }

    authToken.validate();
    twoFactorAuthTokenRepo.invalidateAllByUserEmail(email);

    var accessToken = jwtService.generateAccesToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    tokenRepo.save(accessToken);
    tokenRepo.save(refreshToken);
    return new AuthResponse(
      accessToken.getTokenHash(),
      refreshToken.getTokenHash()
    );
  }

  private User getUserByEmail(String email) {
    return userRepo.findByEmail(email).orElseThrow(UserNotFoundException::new);
  }

  private TwoFactorAuthToken getTwoFactorAuthTokenByEmail(String email) {
    return twoFactorAuthTokenRepo
      .findLastByUserEmail(email)
      .orElseThrow(() ->
        new InvalidTwoFactorAuthTokenException(
          "No hay ningun codigo activo para tu email"
        )
      );
  }
}
