package com.fabrica.authentication.application;

import com.fabrica.authentication.application.dto.UserMessage;
import com.fabrica.authentication.domain.exceptions.UserMessageException;
import com.fabrica.authentication.infrastructure.database.entities.TokenType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthDtoTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void userMessageBuilderConstruyeCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Act
        UserMessage msg = UserMessage.builder()
                .userId(id)
                .name("Juan")
                .lastname("Pérez")
                .passwordHash("hash123")
                .email("juan@test.com")
                .createdAt(now)
                .build();

        // Assert
        assertEquals(id, msg.userId());
        assertEquals("Juan", msg.name());
        assertEquals("Pérez", msg.lastname());
        assertEquals("hash123", msg.passwordHash());
        assertEquals("juan@test.com", msg.email());
        assertEquals(now, msg.createdAt());
    }

    @Test
    void tokenTypeContieneValoresAccesYRefresh() {
        // Arrange - Act
        TokenType[] values = TokenType.values();

        // Assert
        assertEquals(2, values.length);
        assertEquals(TokenType.ACCESS, TokenType.valueOf("ACCESS"));
        assertEquals(TokenType.REFRESH, TokenType.valueOf("REFRESH"));
    }

    @Test
    void userMessageExceptionContieneTextoDescriptivo() {
        // Arrange - Act
        UserMessageException ex = new UserMessageException("fallo de conexion");

        // Assert
        assertTrue(ex.getMessage().contains("fallo de conexion"));
    }
}
