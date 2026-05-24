package com.fabrica.authentication.infrastructure;

import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.TokenType;
import com.fabrica.authentication.infrastructure.web.config.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String SECRET = "una-clave-de-prueba-de-al-menos-32-bytes-largo";

    @Mock private TokenRepositoryPort tokenRepo;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(tokenRepo);
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "accessExpiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 86_400_000L);
    }

    private User usuarioValido() {
        return User.builder()
            .userId(UUID.randomUUID())
            .email("u@mail.com")
            .name("Ana")
            .lastname("Lopez")
            .passwordHash("hash")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void generateAccesTokenConstruyeTokenAccessConDatosDelUsuario() {
        // Arrange
        User user = usuarioValido();

        // Act
        Token token = jwtService.generateAccesToken(user);

        // Assert
        assertNotNull(token.getTokenHash());
        assertNotNull(token.getTokenId());
        assertEquals(TokenType.ACCESS.toString(), token.getTokenType());
        assertEquals(user, token.getUser());
        assertNotNull(token.getExpirationDate());
    }

    @Test
    void generateRefreshTokenConstruyeTokenRefreshConDatosDelUsuario() {
        // Arrange
        User user = usuarioValido();

        // Act
        Token token = jwtService.generateRefreshToken(user);

        // Assert
        assertNotNull(token.getTokenHash());
        assertEquals(TokenType.REFRESH.toString(), token.getTokenType());
        assertEquals(user, token.getUser());
    }

    @Test
    void isTokenValidRetornaTrueCuandoExisteYNoEstaExpirado() {
        // Arrange
        User user = usuarioValido();
        Token generado = jwtService.generateAccesToken(user);
        Token persistido = Token.builder()
            .tokenId(generado.getTokenId())
            .tokenHash(generado.getTokenHash())
            .expirationDate(LocalDateTime.now().plusHours(1))
            .tokenType(TokenType.ACCESS.toString())
            .user(user)
            .build();
        when(tokenRepo.findByHash(generado.getTokenHash())).thenReturn(Optional.of(persistido));

        // Act - Assert
        assertTrue(jwtService.isTokenValid(generado));
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void isTokenValidRetornaFalseCuandoNoExisteEnRepo() {
        // Arrange
        Token token = jwtService.generateAccesToken(usuarioValido());
        when(tokenRepo.findByHash(token.getTokenHash())).thenReturn(Optional.empty());

        // Act - Assert
        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValidRetornaFalseCuandoEstaExpiradoEnRepo() {
        // Arrange
        User user = usuarioValido();
        Token generado = jwtService.generateAccesToken(user);
        Token expirado = Token.builder()
            .tokenId(generado.getTokenId())
            .tokenHash(generado.getTokenHash())
            .expirationDate(LocalDateTime.now().minusMinutes(1))
            .tokenType(TokenType.ACCESS.toString())
            .user(user)
            .build();
        when(tokenRepo.findByHash(generado.getTokenHash())).thenReturn(Optional.of(expirado));

        // Act - Assert
        assertFalse(jwtService.isTokenValid(generado));
    }
}
