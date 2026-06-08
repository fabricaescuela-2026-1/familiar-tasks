package com.fabricaescuela.logs.application;

import com.fabricaescuela.logs.application.service.LogService;
import com.fabricaescuela.logs.domain.model.Log;
import com.fabricaescuela.logs.domain.ports.out.LogRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock private LogRepositoryPort logRepository;

    @InjectMocks
    private LogService logService;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void ejecutarCreacionDeLogGuardaYRetornaLog() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        Log logEsperado = new Log(id, userId, LocalDateTime.of(2026, 1, 1, 10, 0, 0), "ROLE", "CHANGED");
        when(logRepository.save(any(Log.class))).thenReturn(logEsperado);

        // Act
        Log resultado = logService.execute(id, userId, "ROLE", "CHANGED");

        // Assert
        assertNotNull(resultado);
        assertEquals(userId, resultado.idUser());
        assertEquals("ROLE", resultado.modifiedElement());
        assertEquals("CHANGED", resultado.action());
        verify(logRepository).save(any(Log.class));
    }

    @Test
    void obtenerTodosLosLogsRetornaLista() {
        // Arrange
        Log log1 = new Log(UUID.randomUUID().toString(), "u1", LocalDateTime.of(2026, 1, 1, 10, 0, 0), "TASK", "CREATED");
        Log log2 = new Log(UUID.randomUUID().toString(), "u2", LocalDateTime.of(2026, 1, 1, 10, 0, 0), "ROLE", "CHANGED");
        when(logRepository.findAll()).thenReturn(List.of(log1, log2));

        // Act
        List<Log> resultado = logService.getAllLogs();

        // Assert
        assertEquals(2, resultado.size());
        verify(logRepository).findAll();
    }

    @Test
    void obtenerLogsVaciosRetornaListaVacia() {
        // Arrange
        when(logRepository.findAll()).thenReturn(List.of());

        // Act
        List<Log> resultado = logService.getAllLogs();

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void crearLogConUsuarioNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(NullPointerException.class, () ->
            logService.execute(null, null, "ROLE", "CHANGED")
        );
        verify(logRepository, never()).save(any());
    }

    @Test
    void crearLogConElementoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(NullPointerException.class, () ->
            logService.execute(null, "userId", null, "CHANGED")
        );
        verify(logRepository, never()).save(any());
    }

    @Test
    void crearLogConAccionNulaLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(NullPointerException.class, () ->
            logService.execute(null, "userId", "ROLE", null)
        );
        verify(logRepository, never()).save(any());
    }

    @Test
    void crearLogConUserIdVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(IllegalArgumentException.class, () ->
            logService.execute(null, "   ", "ROLE", "CHANGED")
        );
        verify(logRepository, never()).save(any());
    }

    // HU27 Scenario 1 — la acción registrada debe incluir usuario, elemento y acción
    @Test
    void logCreadoConservaUsuarioElementoYAccion() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        when(logRepository.save(any(Log.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        logService.execute(null, userId, "TASK", "UPDATED");

        // Assert
        verify(logRepository).save(captor.capture());
        Log persistido = captor.getValue();
        assertEquals(userId,    persistido.idUser());
        assertEquals("TASK",    persistido.modifiedElement());
        assertEquals("UPDATED", persistido.action());
    }

    // HU27 Scenario 1 — el sistema debe asignar automáticamente la hora del registro
    @Test
    void logCreadoIncluyeTimestampAutomatico() {
        // Arrange
        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        when(logRepository.save(any(Log.class))).thenAnswer(inv -> inv.getArgument(0));
        LocalDateTime antes = LocalDateTime.MIN;

        // Act
        logService.execute(null, "u1", "TASK", "DELETED");

        // Assert
        verify(logRepository).save(captor.capture());
        Log persistido = captor.getValue();
        assertNotNull(persistido.timestamp());
        assertTrue(persistido.timestamp().isAfter(antes));
        assertTrue(persistido.timestamp().isBefore(LocalDateTime.MAX));
    }

    // HU27 Scenario 2 — consultar logs muestra todas las entradas con sus datos completos
    @Test
    void getAllLogsRetornaListaConDatosCompletos() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        Log log = new Log(UUID.randomUUID().toString(), userId, LocalDateTime.of(2026, 1, 1, 10, 0, 0), "TASK", "UPDATED");
        when(logRepository.findAll()).thenReturn(List.of(log));

        // Act
        List<Log> resultado = logService.getAllLogs();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(userId,    resultado.get(0).idUser());
        assertEquals("UPDATED", resultado.get(0).action());
        assertNotNull(resultado.get(0).timestamp());
    }
}
