package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.infraestructure.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

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
        signingKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private Authentication autenticacionDe(String usuario, String... roles) {
        var authorities = java.util.Arrays.stream(roles)
            .map(SimpleGrantedAuthority::new).toList();
        return new UsernamePasswordAuthenticationToken(usuario, "pwd", authorities);
    }

    private String construirToken(String subject, long expirationMs) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(signingKey)
            .compact();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void createTokenGeneraJwtFirmadoConClaims() {
        // Arrange
        Authentication auth = autenticacionDe("user@mail.com", "ROLE_ADMIN", "ROLE_USER");

        // Act
        String token = jwtUtils.createToken(auth);
        Claims claims = jwtUtils.validateToken(token);

        // Assert
        assertEquals("user@mail.com", claims.getSubject());
        assertTrue(claims.get("roles", String.class).contains("ROLE_ADMIN"));
        assertNotNull(claims.getId());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void validateTokenRetornaClaimsDelTokenValido() {
        // Arrange
        String token = construirToken("user@mail.com", 60_000);

        // Act
        Claims claims = jwtUtils.validateToken(token);

        // Assert
        assertEquals("user@mail.com", claims.getSubject());
    }

    @Test
    void extractUsernameRetornaSubject() {
        // Arrange
        Claims claims = jwtUtils.validateToken(construirToken("u@mail.com", 60_000));

        // Act - Assert
        assertEquals("u@mail.com", jwtUtils.extractUsername(claims));
    }

    @Test
    void getSpecificClaimRetornaValorDelClaim() {
        // Arrange
        String token = jwtUtils.createToken(autenticacionDe("u@mail.com", "ROLE_ADMIN"));
        Claims claims = jwtUtils.validateToken(token);

        // Act - Assert
        assertEquals("ROLE_ADMIN", jwtUtils.getSpecificClaim(claims, "roles"));
    }

    @Test
    void getAllClaimsRetornaMapaConTodosLosClaims() {
        // Arrange
        Claims claims = jwtUtils.validateToken(construirToken("u@mail.com", 60_000));

        // Act
        Map<String, Object> todos = jwtUtils.getAllClaims(claims);

        // Assert
        assertEquals("u@mail.com", todos.get("sub"));
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void validateTokenExpiradoLanzaExpiredJwtException() {
        // Arrange
        String tokenExpirado = construirToken("u@mail.com", -1000);

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
