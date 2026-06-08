package com.fabricaescuela.logs.domain;

import com.fabricaescuela.logs.domain.model.Log;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LogModelTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void creacionExitosaConDatosValidos() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 1, 10, 0, 0);

        // Act
        Log log = new Log(id, userId, timestamp, "TASK", "CREATED");

        // Assert
        assertEquals(id, log.id());
        assertEquals(userId, log.idUser());
        assertEquals(timestamp, log.timestamp());
        assertEquals("TASK", log.modifiedElement());
        assertEquals("CREATED", log.action());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void userIdNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(NullPointerException.class, () ->
            new Log(null, null, LocalDateTime.of(2026, 1, 1, 10, 0, 0), "TASK", "CREATED")
        );
    }

    @Test
    void elementoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(NullPointerException.class, () ->
            new Log(null, "userId", LocalDateTime.of(2026, 1, 1, 10, 0, 0), null, "CREATED")
        );
    }

    @Test
    void accionNulaLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(NullPointerException.class, () ->
            new Log(null, "userId", LocalDateTime.of(2026, 1, 1, 10, 0, 0), "TASK", null)
        );
    }

    @Test
    void userIdVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(IllegalArgumentException.class, () ->
            new Log(null, "   ", LocalDateTime.of(2026, 1, 1, 10, 0, 0), "TASK", "CREATED")
        );
    }
}
