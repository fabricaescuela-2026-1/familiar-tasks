package com.fabrica.authentication.infrastructure.web.config;

import java.security.Key;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.TokenType;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtServicePort {
  private final TokenRepositoryPort tokenRepo;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access.expiration}")
  private long accessExpiration;

  @Value("${jwt.refresh.expiration}")
  private long refreshExpiration;

  @Override
  public Token generateAccesToken(User user) {
    var expriationDate = new Date(System.currentTimeMillis() + accessExpiration);

    JwtBuilder builder = Jwts.builder()
        .setSubject(user.getEmail())
        .setId(user.getUserId().toString())
        .signWith(getKey())
        .setExpiration(expriationDate);
    return Token.builder()
        .tokenHash(builder.compact())
        .tokenId(UUID.randomUUID())
        .expiratedAt(null)
        .expirationDate(expriationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        .tokenType(TokenType.ACCESS.toString())
        .user(user)
        .build();
  }

  @Override
  public Token generateRefreshToken(User user) {
    var expriationDate = new Date(System.currentTimeMillis() + refreshExpiration);
    JwtBuilder builder = Jwts.builder()
        .setId(user.getUserId().toString())
        .setSubject(user.getEmail())
        .signWith(getKey())
        .setExpiration(expriationDate);
    return Token.builder()
        .tokenHash(builder.compact())
        .tokenId(UUID.randomUUID())
        .expiratedAt(null)
        .expirationDate(expriationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        .tokenType(TokenType.REFRESH.toString())
        .user(user)
        .build();
  }

  @Override
  public boolean isTokenValid(Token token) {
    String hash = token.getTokenHash();
    Optional<Token> dbToken = tokenRepo.findByHash(hash);
    if (dbToken.isEmpty()) {
      return false;
    }
    return !dbToken.get().getExpirationDate().isBefore(java.time.LocalDateTime.now())
        && getEmailFromTokenHash(hash).equals(dbToken.get().getUser().getEmail())
        && getUserIdFromTokenHash(hash).equals(dbToken.get().getUser().getUserId().toString());
  }

  private String getEmailFromTokenHash(String tokenHash) {
    return Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(tokenHash)
        .getBody()
        .getSubject();
  }

  private String getUserIdFromTokenHash(String tokenHash) {
    return Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(tokenHash)
        .getBody()
        .getId();
  }

  private Key getKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }
}
