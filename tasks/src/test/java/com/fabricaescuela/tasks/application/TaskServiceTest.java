package com.fabricaescuela.tasks.application;

import com.fabricaescuela.tasks.application.dto.TaskSearchCriteria;
import com.fabricaescuela.tasks.domain.exceptions.ForbiddenTaskOperationException;
import com.fabricaescuela.tasks.domain.exceptions.TaskNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.UserNotValidException;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import com.fabricaescuela.tasks.domain.ports.out.TaskRepositoryPort;
import com.fabricaescuela.tasks.domain.ports.out.UserLookupPort;
import com.fabricaescuela.tasks.domain.ports.out.UserValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepositoryPort repository;
    @Mock private UserValidationPort userValidation;
    @Mock private TaskAuditLogPort auditLog;
    @Mock private UserLookupPort userLookup;

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

    // HU17 Scenario 2 — guardar una edición con título vacío debe ser rechazado
    @Test
    void actualizacionConTituloVacioLanzaExcepcion() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        tarea.setName("");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.update(taskId, tarea));
        verify(repository, never()).update(any(), any());
    }

    // HU17 Scenario 2 — guardar una edición con título nulo debe ser rechazado
    @Test
    void actualizacionConTituloNuloLanzaExcepcion() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        tarea.setName(null);

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.update(taskId, tarea));
        verify(repository, never()).update(any(), any());
    }

    // HU17 — al actualizar, una fecha en el pasado sigue siendo inválida
    @Test
    void actualizacionConFechaPasadaLanzaExcepcion() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        tarea.setDeadline(LocalDateTime.now().minusDays(1));

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.update(taskId, tarea));
        verify(repository, never()).update(any(), any());
    }

    // HU17 — los datos actualizados deben quedar persistidos en el repositorio
    @Test
    void actualizacionExitosaPersisteLosNuevosValores() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        tarea.setName("Sacar la basura");
        tarea.setDescription("Sacar las bolsas antes de las 7pm");
        when(userValidation.validateUserInHome(tarea.getGuestId(), tarea.getHomeId())).thenReturn(true);
        when(repository.update(taskId, tarea)).thenReturn(tarea);

        // Act
        Task resultado = taskService.update(taskId, tarea);

        // Assert
        assertEquals("Sacar la basura", resultado.getName());
        assertEquals("Sacar las bolsas antes de las 7pm", resultado.getDescription());
        verify(repository).update(taskId, tarea);
    }

    // HU18 Scenario 1 — eliminar una tarea por id la quita del repositorio
    @Test
    void eliminacionConIdValidoDelegaAlRepositorio() {
        // Arrange
        UUID taskId = UUID.randomUUID();

        // Act
        taskService.delete(taskId);

        // Assert
        verify(repository).delete(taskId);
    }

    // HU28 Scenario 1 — al actualizar una tarea debe quedar registro en el audit log
    @Test
    void actualizacionDeTareaPublicaAuditLog() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        when(userValidation.validateUserInHome(tarea.getGuestId(), tarea.getHomeId())).thenReturn(true);
        when(repository.update(taskId, tarea)).thenReturn(tarea);

        // Act
        taskService.update(taskId, tarea);

        // Assert
        verify(auditLog).publishTaskUpdated(tarea.getGuestId(), taskId);
    }

    // HU28 Scenario 1 — al eliminar una tarea debe quedar registro en el audit log
    @Test
    void eliminacionDeTareaPublicaAuditLog() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        when(repository.findById(taskId)).thenReturn(Optional.of(tarea));

        // Act
        taskService.delete(taskId);

        // Assert
        verify(auditLog).publishTaskDeleted(tarea.getGuestId(), taskId);
    }

    // HU19-búsqueda Scenario 1 — búsqueda con keyword retorna lista filtrada del repositorio
    @Test
    void busquedaConKeywordRetornaListaFiltrada() {
        // Arrange
        TaskSearchCriteria criteria = new TaskSearchCriteria("barrer");
        List<Task> tareas = List.of(tareaValida());
        when(repository.search(criteria)).thenReturn(tareas);

        // Act
        List<Task> resultado = taskService.search(criteria);

        // Assert
        assertEquals(1, resultado.size());
        verify(repository).search(criteria);
    }

    // HU19-búsqueda Scenario 2 — sin coincidencias retorna lista vacía con delegación al repositorio
    @Test
    void busquedaSinCoincidenciasRetornaListaVacia() {
        // Arrange
        TaskSearchCriteria criteria = new TaskSearchCriteria("xyz_no_existe");
        when(repository.search(criteria)).thenReturn(List.of());

        // Act
        List<Task> resultado = taskService.search(criteria);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(repository).search(criteria);
    }

    // HU19-búsqueda Scenario 3 — keyword vacío delega al repositorio sin filtro adicional
    @Test
    void busquedaConKeywordVacioRetornaTodasLasTareas() {
        // Arrange
        TaskSearchCriteria criteria = new TaskSearchCriteria("");
        List<Task> tareas = List.of(tareaValida(), tareaValida());
        when(repository.search(criteria)).thenReturn(tareas);

        // Act
        List<Task> resultado = taskService.search(criteria);

        // Assert
        assertEquals(2, resultado.size());
        verify(repository).search(criteria);
    }

    // HU20 Scenario 1 — el asignado cambia el estado: persiste, retorna y publica audit log
    @Test
    void changeStatusExitosoCuandoLoEjecutaElAsignado() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String username = "tomas@familia.com";
        Task tarea = Task.builder().taskId(taskId).guestId(userId).status("PENDIENTE").build();
        Task actualizada = Task.builder().taskId(taskId).guestId(userId).status("EN_PROGRESO").build();
        when(userLookup.findUserIdByUsername(username)).thenReturn(Optional.of(userId));
        when(repository.findById(taskId)).thenReturn(Optional.of(tarea));
        when(repository.updateStatus(taskId, "EN_PROGRESO")).thenReturn(actualizada);

        // Act
        Task resultado = taskService.changeStatus(taskId, "EN_PROGRESO", username);

        // Assert
        assertEquals("EN_PROGRESO", resultado.getStatus());
        verify(repository).updateStatus(taskId, "EN_PROGRESO");
        verify(auditLog).publishTaskStatusChanged(userId, taskId, "EN_PROGRESO");
    }

    // HU20 Scenario 3 — quien NO es el asignado recibe ForbiddenTaskOperationException
    @Test
    void changeStatusFallaCuandoElUsuarioNoEsElAsignado() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID assignedUserId = UUID.randomUUID();
        UUID otroUserId = UUID.randomUUID();
        String username = "gabriela@familia.com";
        Task tarea = Task.builder().taskId(taskId).guestId(assignedUserId).status("PENDIENTE").build();
        when(userLookup.findUserIdByUsername(username)).thenReturn(Optional.of(otroUserId));
        when(repository.findById(taskId)).thenReturn(Optional.of(tarea));

        // Act - Assert
        assertThrows(ForbiddenTaskOperationException.class,
            () -> taskService.changeStatus(taskId, "EN_PROGRESO", username));
        verify(repository, never()).updateStatus(any(), any());
        verify(auditLog, never()).publishTaskStatusChanged(any(), any(), any());
    }

    // HU20 — si la tarea no existe, lanza TaskNotFoundException
    @Test
    void changeStatusFallaCuandoLaTareaNoExiste() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String username = "tomas@familia.com";
        when(userLookup.findUserIdByUsername(username)).thenReturn(Optional.of(userId));
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(TaskNotFoundException.class,
            () -> taskService.changeStatus(taskId, "EN_PROGRESO", username));
        verify(repository, never()).updateStatus(any(), any());
        verify(auditLog, never()).publishTaskStatusChanged(any(), any(), any());
    }

    // HU20 — si no se puede resolver el usuario (sin auth), lanza ForbiddenTaskOperationException
    @Test
    void changeStatusFallaCuandoNoHayUsuarioAutenticado() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        when(userLookup.findUserIdByUsername(null)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(ForbiddenTaskOperationException.class,
            () -> taskService.changeStatus(taskId, "EN_PROGRESO", null));
        verify(repository, never()).findById(any());
        verify(repository, never()).updateStatus(any(), any());
    }

    // HU20 — un estado nulo o vacío es rechazado antes de tocar repositorio
    @Test
    void changeStatusFallaCuandoElNuevoEstadoEsVacio() {
        UUID taskId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
            () -> taskService.changeStatus(taskId, "", "tomas@familia.com"));
        verifyNoInteractions(repository, auditLog, userLookup);
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
