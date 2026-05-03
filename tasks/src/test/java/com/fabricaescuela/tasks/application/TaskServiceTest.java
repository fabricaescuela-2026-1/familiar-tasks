package com.fabricaescuela.tasks.application;

import com.fabricaescuela.tasks.domain.exceptions.UserNotValidException;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import com.fabricaescuela.tasks.domain.ports.out.TaskRepositoryPort;
import com.fabricaescuela.tasks.domain.ports.out.UserValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepositoryPort repository;
    @Mock private UserValidationPort userValidation;
    @Mock private TaskAuditLogPort auditLog;

    @InjectMocks
    private TaskService taskService;

    private Task tareaValida() {
        return Task.builder()
                .name("Barrer patio")
                .description("Barrer hojas del patio trasero")
                .status("PENDIENTE")
                .priority("MEDIA")
                .deadline(LocalDateTime.now().plusDays(2))
                .homeId(UUID.randomUUID())
                .guestId(UUID.randomUUID())
                .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void creacionExitosaCuandoUsuarioPerteneceAlHogar() {
        // Arrange
        Task tarea = tareaValida();
        UUID taskId = UUID.randomUUID();
        Task tareaGuardada = Task.builder()
                .taskId(taskId)
                .name(tarea.getName())
                .description(tarea.getDescription())
                .status(tarea.getStatus())
                .priority(tarea.getPriority())
                .deadline(tarea.getDeadline())
                .homeId(tarea.getHomeId())
                .guestId(tarea.getGuestId())
                .build();
        when(userValidation.validateUserInHome(tarea.getGuestId(), tarea.getHomeId())).thenReturn(true);
        when(repository.save(tarea)).thenReturn(tareaGuardada);

        // Act
        Task resultado = taskService.create(tarea);

        // Assert
        assertEquals(tareaGuardada, resultado);
        verify(repository).save(tarea);
        verify(auditLog).publishTaskCreated(tarea.getGuestId(), taskId);
    }

    @Test
    void findAllRetornaListaDelRepositorio() {
        // Arrange
        List<Task> tareas = List.of(tareaValida(), tareaValida());
        when(repository.findAll()).thenReturn(tareas);

        // Act
        List<Task> resultado = taskService.findAll();

        // Assert
        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void creacionFallaSiTareaEsInvalida() {
        // Arrange
        Task tareaInvalida = Task.builder().build();

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.create(tareaInvalida));
        verifyNoInteractions(repository, userValidation, auditLog);
    }

    @Test
    void creacionFallaSiUsuarioNoEstaEnElHogar() {
        // Arrange
        Task tarea = tareaValida();
        when(userValidation.validateUserInHome(tarea.getGuestId(), tarea.getHomeId())).thenReturn(false);

        // Act - Assert
        assertThrows(UserNotValidException.class, () -> taskService.create(tarea));
        verify(repository, never()).save(any());
        verify(auditLog, never()).publishTaskCreated(any(), any());
    }

    @Test
    void actualizacionExitosaCuandoUsuarioPerteneceAlHogar() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        when(userValidation.validateUserInHome(tarea.getGuestId(), tarea.getHomeId())).thenReturn(true);
        when(repository.update(taskId, tarea)).thenReturn(tarea);

        // Act
        Task resultado = taskService.update(taskId, tarea);

        // Assert
        assertEquals(tarea, resultado);
        verify(repository).update(taskId, tarea);
    }

    @Test
    void eliminacionExitosaLlamaAlRepositorio() {
        // Arrange
        UUID taskId = UUID.randomUUID();

        // Act
        taskService.delete(taskId);

        // Assert
        verify(repository).delete(taskId);
    }

    @Test
    void actualizacionFallaSiUsuarioNoEstaEnElHogar() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        when(userValidation.validateUserInHome(tarea.getGuestId(), tarea.getHomeId())).thenReturn(false);

        // Act - Assert
        assertThrows(UserNotValidException.class, () -> taskService.update(taskId, tarea));
        verify(repository, never()).update(any(), any());
    }

    // HU13 Scenario 1 — la tarea creada sin estado explícito recibe PENDIENTE por defecto
    @Test
    void tareaCreadaSinEstadoUsaPendientePorDefecto() {
        // Arrange
        Task tarea = Task.builder()
                .name("Barrer patio")
                .description("Barrer hojas del patio trasero")
                .priority("MEDIA")
                .deadline(LocalDateTime.now().plusDays(2))
                .homeId(UUID.randomUUID())
                .guestId(UUID.randomUUID())
                .build();

        when(userValidation.validateUserInHome(any(), any())).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Task resultado = assertDoesNotThrow(() -> taskService.create(tarea));

        // Assert
        assertEquals("PENDIENTE", resultado.getStatus());
    }
}
