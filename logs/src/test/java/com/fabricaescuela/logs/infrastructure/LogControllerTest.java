package com.fabricaescuela.logs.infrastructure;

import com.fabricaescuela.logs.application.dto.LogRequest;
import com.fabricaescuela.logs.application.dto.LogResponse;
import com.fabricaescuela.logs.application.mappers.LogMapper;
import com.fabricaescuela.logs.application.service.LogService;
import com.fabricaescuela.logs.domain.model.Log;
import com.fabricaescuela.logs.infrastructure.adapter.in.web.LogController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock private LogService logService;
    @Mock private LogMapper logMapper;

    private LogController controller;

    @BeforeEach
    void setUp() {
        controller = new LogController(logService, logService, logMapper);
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void createLogRetorna201ConRegistroPersistido() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        LogRequest request = new LogRequest(id, userId, "TASK", "CREATED");
        Log log = new Log(id, userId, LocalDateTime.now(), "TASK", "CREATED");
        LogResponse logResponse = new LogResponse(id, userId, log.timestamp(), "TASK", "CREATED");
        when(logService.execute(id, userId, "TASK", "CREATED")).thenReturn(log);
        when(logMapper.toResponse(log)).thenReturn(logResponse);

        // Act
        ResponseEntity<LogResponse> response = controller.createLog(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("TASK", response.getBody().modifiedElement());
        verify(logService).execute(id, userId, "TASK", "CREATED");
    }

    @Test
    void getAllLogsRetornaListaMapeada() {
        // Arrange
        Log log1 = new Log("1", "u1", LocalDateTime.now(), "TASK", "CREATED");
        Log log2 = new Log("2", "u2", LocalDateTime.now(), "ROLE", "CHANGED");
        LogResponse r1 = new LogResponse("1", "u1", log1.timestamp(), "TASK", "CREATED");
        LogResponse r2 = new LogResponse("2", "u2", log2.timestamp(), "ROLE", "CHANGED");
        when(logService.getAllLogs()).thenReturn(List.of(log1, log2));
        when(logMapper.toResponse(log1)).thenReturn(r1);
        when(logMapper.toResponse(log2)).thenReturn(r2);

        // Act
        ResponseEntity<List<LogResponse>> response = controller.getAllLogs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllLogsConRepositorioVacioRetornaListaVacia() {
        // Arrange
        when(logService.getAllLogs()).thenReturn(List.of());

        // Act
        ResponseEntity<List<LogResponse>> response = controller.getAllLogs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}
