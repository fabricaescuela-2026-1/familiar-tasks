package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.infrastructure.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String SECRET = "una-clave-de-prueba-de-al-menos-32-bytes-largo";

    private JwtUtils jwtUtils;
    private Key signingKey;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secretKey", SECRET);
        signingKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String construirToken(String subject, String roles, long expirationMs) {
        return Jwts.builder()
            .setSubject(subject)
            .claim("roles", roles)
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(signingKey)
            .compact();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void validateTokenRetornaClaimsDelTokenValido() {
        // Arrange
        String token = construirToken("user@mail.com", "ADMIN", 60_000);

        // Act
        Claims claims = jwtUtils.validateToken(token);

        // Assert
        assertEquals("user@mail.com", claims.getSubject());
        assertEquals("ADMIN", claims.get("roles", String.class));
    }

    @Test
    void extractUsernameRetornaSubjectDelClaim() {
        // Arrange
        Claims claims = jwtUtils.validateToken(construirToken("u@mail.com", "USER", 60_000));

        // Act
        String username = jwtUtils.extractUsername(claims);

        // Assert
        assertEquals("u@mail.com", username);
    }

    @Test
    void getSpecificClaimRetornaValorDelClaim() {
        // Arrange
        Claims claims = jwtUtils.validateToken(construirToken("u@mail.com", "USER,ADMIN", 60_000));

        // Act
        String roles = jwtUtils.getSpecificClaim(claims, "roles");

        // Assert
        assertEquals("USER,ADMIN", roles);
    }

    @Test
    void getAllClaimsRetornaMapaConTodosLosClaims() {
        // Arrange
        Claims claims = jwtUtils.validateToken(construirToken("u@mail.com", "ADMIN", 60_000));

        // Act
        Map<String, Object> todos = jwtUtils.getAllClaims(claims);

        // Assert
        assertEquals("u@mail.com", todos.get("sub"));
        assertEquals("ADMIN", todos.get("roles"));
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void validateTokenExpiradoLanzaExpiredJwtException() {
        // Arrange
        String tokenExpirado = construirToken("u@mail.com", "USER", -1000);

        // Act - Assert
        ExpiredJwtException ex = assertThrows(ExpiredJwtException.class,
            () -> jwtUtils.validateToken(tokenExpirado));
        assertEquals("JWT token has expired", ex.getMessage());
    }

    @Test
    void validateTokenInvalidoLanzaJwtException() {
        // Arrange - Act - Assert
        JwtException ex = assertThrows(JwtException.class,
            () -> jwtUtils.validateToken("token-malformado"));
        assertTrue(ex.getMessage().contains("Invalid JWT token"));
    }
}
