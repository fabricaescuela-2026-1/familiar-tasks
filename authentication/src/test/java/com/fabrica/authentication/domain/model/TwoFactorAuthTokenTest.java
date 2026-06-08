package com.fabrica.authentication.domain.model;

import com.fabrica.authentication.domain.exceptions.InvalidTowFactorAuthTokenException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TwoFactorAuthTokenTest {

    private TwoFactorAuthToken tokenBase() {
        return TwoFactorAuthToken.builder()
            .id(UUID.randomUUID())
            .codeHash("hash")
            .user(User.builder().email("ana@mail.com").build())
            .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0, 0))
            .expiresAt(LocalDateTime.of(2099, 1, 1, 10, 10, 0))
            .invalidated(false)
            .attempts(0)
            .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void builderYGettersExponenLosCamposCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = User.builder().email("u@mail.com").build();

        // Act
        TwoFactorAuthToken token = TwoFactorAuthToken.builder()
            .id(id)
            .codeHash("h")
            .user(user)
            .attempts(2)
            .invalidated(false)
            .expiresAt(LocalDateTime.of(2026, 1, 1, 10, 5, 0))
            .build();

        // Assert
        assertEquals(id, token.getId());
        assertEquals("h", token.getCodeHash());
        assertEquals(user, token.getUser());
        assertEquals(2, token.getAttempts());
        assertFalse(token.isInvalidated());
    }

    @Test
    void validateNoLanzaCuandoTokenEsValido() {
        assertDoesNotThrow(tokenBase()::validate);
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void validateFallaCuandoEstaInvalidado() {
        TwoFactorAuthToken token = tokenBase();
        token.setInvalidated(true);
        assertThrows(InvalidTowFactorAuthTokenException.class, token::validate);
    }

    @Test
    void validateFallaCuandoTokenExpiro() {
        TwoFactorAuthToken token = tokenBase();
        token.setExpiresAt(LocalDateTime.of(2026, 1, 1, 9, 59, 0));
        assertThrows(InvalidTowFactorAuthTokenException.class, token::validate);
    }

    @Test
    void validateFallaCuandoAttemptsAlcanzaTres() {
        TwoFactorAuthToken token = tokenBase();
        token.setAttempts(3);
        assertThrows(InvalidTowFactorAuthTokenException.class, token::validate);
    }
}
