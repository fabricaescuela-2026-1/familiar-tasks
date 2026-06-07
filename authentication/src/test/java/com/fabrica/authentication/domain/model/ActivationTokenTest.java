package com.fabrica.authentication.domain.model;

import com.fabrica.authentication.domain.exceptions.InvalidActivationTokenException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActivationTokenTest {

    private ActivationToken tokenBase() {
        return ActivationToken.builder()
            .id(UUID.randomUUID())
            .email("ana@mail.com")
            .codeHash("hash")
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(10))
            .attempts(0)
            .invalidated(false)
            .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void builderYGettersExponenLosCamposCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime expires = created.plusMinutes(15);

        // Act
        ActivationToken token = ActivationToken.builder()
            .id(id)
            .email("a@mail.com")
            .codeHash("h")
            .createdAt(created)
            .expiresAt(expires)
            .attempts(1)
            .invalidated(false)
            .build();

        // Assert
        assertEquals(id, token.getId());
        assertEquals("a@mail.com", token.getEmail());
        assertEquals("h", token.getCodeHash());
        assertEquals(created, token.getCreatedAt());
        assertEquals(expires, token.getExpiresAt());
        assertEquals(1, token.getAttempts());
        assertFalse(token.isInvalidated());
    }

    @Test
    void validateTokenNoLanzaCuandoTokenEsValido() {
        // Arrange
        ActivationToken token = tokenBase();

        // Act - Assert
        assertDoesNotThrow(token::validateToken);
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void validateTokenFallaCuandoAttemptsAlcanzaTres() {
        // Arrange
        ActivationToken token = tokenBase();
        token.setAttempts(3);

        // Act - Assert
        assertThrows(InvalidActivationTokenException.class, token::validateToken);
    }

    @Test
    void validateTokenFallaCuandoTokenExpiro() {
        // Arrange
        ActivationToken token = tokenBase();
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        // Act - Assert
        assertThrows(InvalidActivationTokenException.class, token::validateToken);
    }

    @Test
    void validateTokenFallaCuandoEstaInvalidado() {
        // Arrange
        ActivationToken token = tokenBase();
        token.setInvalidated(true);

        // Act - Assert
        assertThrows(InvalidActivationTokenException.class, token::validateToken);
    }
}
