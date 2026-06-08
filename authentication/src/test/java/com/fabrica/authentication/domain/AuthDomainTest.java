package com.fabrica.authentication.domain;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthDomainTest {

    // ── MODELOS DE DOMINIO ──────────────────────────────────────────────────

    @Test
    void userBuilderAsignaTodosLosCampos() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.of(2026, 1, 1, 10, 0, 0);

        // Act
        User user = User.builder()
            .userId(id)
            .name("Pepe")
            .lastname("Gómez")
            .email("pepe@mail.com")
            .passwordHash("hash")
            .isActive(true)
            .createdAt(ahora)
            .build();

        // Assert
        assertEquals(id,          user.getUserId());
        assertEquals("Pepe",      user.getName());
        assertEquals("Gómez",     user.getLastname());
        assertEquals("pepe@mail.com", user.getEmail());
        assertEquals("hash",      user.getPasswordHash());
        assertTrue(user.isActive());
        assertEquals(ahora,       user.getCreatedAt());
    }

    @Test
    void userSettersModificanCampos() {
        // Arrange
        User user = new User();

        // Act
        user.setName("Ana");
        user.setEmail("ana@mail.com");
        user.setActive(false);

        // Assert
        assertEquals("Ana", user.getName());
        assertEquals("ana@mail.com", user.getEmail());
        assertFalse(user.isActive());
    }

    @Test
    void tokenBuilderAsignaTodosLosCampos() {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        LocalDateTime exp = LocalDateTime.of(2026, 1, 1, 11, 0, 0);
        User user = User.builder().email("a@b.co").build();

        // Act
        Token token = Token.builder()
            .tokenId(tokenId)
            .tokenHash("hash")
            .expirationDate(exp)
            .expiratedAt(null)
            .tokenType("ACCESS")
            .user(user)
            .build();

        // Assert
        assertEquals(tokenId, token.getTokenId());
        assertEquals("hash",  token.getTokenHash());
        assertEquals(exp,     token.getExpirationDate());
        assertNull(token.getExpiratedAt());
        assertEquals("ACCESS", token.getTokenType());
        assertEquals(user, token.getUser());
    }

    // ── DTOs ────────────────────────────────────────────────────────────────

    @Test
    void authResponseAlmacenaAmbosTokens() {
        // Arrange - Act
        AuthResponse response = new AuthResponse("access", "refresh");

        // Assert
        assertEquals("access",  response.accessToken());
        assertEquals("refresh", response.refreshToken());
    }

    @Test
    void loginRequestAlmacenaEmailYPassword() {
        // Arrange - Act
        LoginRequest request = new LoginRequest("user@mail.com", "pass");

        // Assert
        assertEquals("user@mail.com", request.email());
        assertEquals("pass", request.password());
    }

    @Test
    void registerRequestAlmacenaTodosLosCampos() {
        // Arrange - Act
        RegisterRequest request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "pass1234");

        // Assert
        assertEquals("Carlos", request.name());
        assertEquals("Ruiz", request.lastname());
        assertEquals("carlos@mail.com", request.email());
        assertEquals("pass1234", request.password());
    }

    @Test
    void tokenResponseBuilderAsignaTodosLosCampos() {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime exp = LocalDateTime.of(2026, 1, 2, 10, 0, 0);

        // Act
        TokenResponse response = TokenResponse.builder()
            .tokenId(tokenId)
            .tokenHash("hash-xyz")
            .expirationDate(exp)
            .userId(userId)
            .tokenType("REFRESH")
            .expiratedAt(null)
            .build();

        // Assert
        assertEquals(tokenId,   response.tokenId());
        assertEquals("hash-xyz", response.tokenHash());
        assertEquals(exp,       response.expirationDate());
        assertEquals(userId,    response.userId());
        assertEquals("REFRESH", response.tokenType());
        assertNull(response.expiratedAt());
    }

    // ── EXCEPCIONES ─────────────────────────────────────────────────────────

    @Test
    void emailAlreadyExitsExceptionContieneEmail() {
        // Arrange - Act
        EmailAlreadyExitsException ex = new EmailAlreadyExitsException("juan@mail.com");

        // Assert
        assertTrue(ex.getMessage().contains("juan@mail.com"));
    }

    @Test
    void invalidRefreshTokenExceptionTieneMensajePorDefecto() {
        // Arrange - Act
        InvalidRefreshTokenException ex = new InvalidRefreshTokenException();

        // Assert
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void invalidTokenExceptionTieneMensajePorDefecto() {
        // Arrange - Act
        InvalidTokenException ex = new InvalidTokenException();

        // Assert
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isBlank());
    }

    @Test
    void userNotFoundExceptionPropagaMensaje() {
        // Arrange - Act
        UserNotFoundException ex = new UserNotFoundException("usuario no existe");

        // Assert
        assertEquals("usuario no existe", ex.getMessage());
    }
}
