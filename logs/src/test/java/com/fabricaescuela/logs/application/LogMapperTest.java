package com.fabricaescuela.logs.application;

import com.fabricaescuela.logs.application.dto.LogResponse;
import com.fabricaescuela.logs.application.mappers.LogMapper;
import com.fabricaescuela.logs.domain.model.Log;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LogMapperTest {

    private final LogMapper mapper = new LogMapper();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toResponseMappeaTodosLosCamposCorrectamente() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();
        Log log = new Log(id, userId, timestamp, "TASK", "CREATED");

        // Act
        LogResponse response = mapper.toResponse(log);

        // Assert
        assertEquals(id,        response.id());
        assertEquals(userId,    response.idUser());
        assertEquals(timestamp, response.timestamp());
        assertEquals("TASK",    response.modifiedElement());
        assertEquals("CREATED", response.action());
    }

    @Test
    void toResponseConIdNuloMappeSinError() {
        // Arrange
        Log log = new Log(null, "userId", LocalDateTime.now(), "ROLE", "CHANGED");

        // Act
        LogResponse response = mapper.toResponse(log);

        // Assert
        assertNull(response.id());
        assertEquals("userId", response.idUser());
    }
}
