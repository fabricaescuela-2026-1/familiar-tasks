package com.fabrica.authentication.acceptance.support;

import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TestUserFactory {

  @Autowired private UserRepositoryPort userRepo;
  @Autowired private TokenRepositoryPort tokenRepo;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtServicePort jwtService;

  public User createUser(String email, String password) {
    User user = User.builder()
        .userId(UUID.randomUUID())
        .name("Usuario")
        .lastname("Prueba")
        .email(email)
        .passwordHash(passwordEncoder.encode(password))
        .isActive(true)
        .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0, 0))
        .build();
    return userRepo.save(user);
  }

  public Token issueValidRefreshToken(User user) {
    Token token = jwtService.generateRefreshToken(user);
    return tokenRepo.save(token);
  }

  public Token issueValidAccessToken(User user) {
    Token token = jwtService.generateAccesToken(user);
    return tokenRepo.save(token);
  }
}
