package com.fabricaescuela.tasks.domain;

import com.fabricaescuela.tasks.application.dto.AuthRefreshResponse;
import com.fabricaescuela.tasks.application.dto.TaskCreatedLog;
import com.fabricaescuela.tasks.application.dto.UserRegistrationEvent;
import com.fabricaescuela.tasks.domain.exceptions.PriorityNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.StatusNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.UserNotValidException;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.MemberHomeDTO;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ProblemDetails;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.RequestTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ResponseTask;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskDomainTest {

    // ── MODELO DE DOMINIO ───────────────────────────────────────────────────

    @Test
    void taskBuilderAsignaTodosLosCampos() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID homeId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        LocalDateTime deadline = LocalDateTime.of(2099, 1, 3, 10, 0, 0);

        // Act
        Task task = Task.builder()
            .taskId(taskId)
            .name("Limpiar")
            .description("Cocina")
            .status("PENDIENTE")
            .priority("ALTA")
            .homeId(homeId)
            .guestId(guestId)
            .deadline(deadline)
            .build();

        // Assert
        assertEquals(taskId,    task.getTaskId());
        assertEquals("Limpiar", task.getName());
        assertEquals("ALTA",    task.getPriority());
        assertEquals(homeId,    task.getHomeId());
        assertEquals(guestId,   task.getGuestId());
        assertEquals(deadline,  task.getDeadline());
    }

    @Test
    void taskSettersModificanCampos() {
        // Arrange
        Task task = new Task();

        // Act
        task.setName("Barrer");
        task.setStatus("EN_PROGRESO");

        // Assert
        assertEquals("Barrer", task.getName());
        assertEquals("EN_PROGRESO", task.getStatus());
    }

    // ── DTOs ────────────────────────────────────────────────────────────────

    @Test
    void requestTaskBuilderAsignaTodosLosCampos() {
        // Arrange
        UUID homeId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        LocalDateTime deadline = LocalDateTime.of(2099, 1, 2, 10, 0, 0);

        // Act
        RequestTask request = RequestTask.builder()
            .name("Tarea")
            .description("Desc")
            .statusName("PENDIENTE")
            .priorityName("MEDIA")
            .homeId(homeId)
            .guestId(guestId)
            .deadline(deadline)
            .build();

        // Assert
        assertEquals("Tarea",     request.getName());
        assertEquals("PENDIENTE", request.getStatusName());
        assertEquals(homeId,      request.getHomeId());
        assertEquals(deadline,    request.getDeadline());
    }

    @Test
    void responseTaskBuilderAsignaTodosLosCampos() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2099, 1, 1, 10, 0, 0);

        // Act
        ResponseTask response = ResponseTask.builder()
            .taskId(taskId)
            .name("Tarea")
            .description("Desc")
            .statusName("PENDIENTE")
            .priorityName("ALTA")
            .homeId(UUID.randomUUID())
            .guestId(UUID.randomUUID())
            .createdAt(now)
            .deadline(now.plusDays(2))
            .build();

        // Assert
        assertEquals(taskId, response.getTaskId());
        assertEquals("ALTA", response.getPriorityName());
        assertEquals(now,    response.getCreatedAt());
    }

    @Test
    void memberHomeDTOAlmacenaTodosLosCampos() {
        // Arrange - Act
        MemberHomeDTO dto = new MemberHomeDTO("home1", "p1", "Carlos", "Ruiz", "Casa", "c@mail.com", true);

        // Assert
        assertEquals("home1",    dto.homeId());
        assertEquals("p1",       dto.personId());
        assertEquals("Carlos",   dto.name());
        assertEquals("Ruiz",     dto.last_name());
        assertEquals("Casa",     dto.homeName());
        assertEquals("c@mail.com", dto.email());
        assertTrue(dto.active());
    }

    @Test
    void problemDetailsBuilderAsignaTodosLosCampos() {
        // Arrange - Act
        ProblemDetails pd = ProblemDetails.builder()
            .type("https://tasks/error")
            .title("Error")
            .status(400)
            .detail("detalle")
            .instance("metodo")
            .build();

        // Assert
        assertEquals("https://tasks/error", pd.getType());
        assertEquals("Error", pd.getTitle());
        assertEquals(400, pd.getStatus());
        assertEquals("detalle", pd.getDetail());
        assertEquals("metodo", pd.getInstance());
    }

    @Test
    void authRefreshResponseAsignaAmbosTokens() {
        // Arrange - Act
        AuthRefreshResponse response = AuthRefreshResponse.builder()
            .accessToken("access")
            .refreshToken("refresh")
            .build();

        // Assert
        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    @Test
    void userRegistrationEventAsignaTodosLosCampos() {
        // Arrange - Act
        UserRegistrationEvent event = new UserRegistrationEvent(
            "uid", "Pepe", "Gómez", "p@mail.com", "hash", "2026-01-01T00:00:00");

        // Assert
        assertEquals("uid",       event.userId());
        assertEquals("Pepe",      event.name());
        assertEquals("p@mail.com", event.email());
        assertEquals("hash",      event.passwordHash());
    }

    @Test
    void taskCreatedLogFactoryConstruyeRegistroDeCreacion() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        // Act
        TaskCreatedLog log = TaskCreatedLog.taskCreated(userId, taskId);

        // Assert
        assertNotNull(log.id());
        assertEquals(userId, log.idUser());
        assertEquals(taskId.toString(), log.modifiedElement());
        assertEquals("task_created", log.action());
    }

    // ── EXCEPCIONES ─────────────────────────────────────────────────────────

    @Test
    void priorityNotFoundExceptionContieneNombre() {
        // Arrange - Act
        PriorityNotFoundException ex = new PriorityNotFoundException("URGENTE");

        // Assert
        assertTrue(ex.getMessage().contains("URGENTE"));
    }

    @Test
    void statusNotFoundExceptionContieneNombre() {
        // Arrange - Act
        StatusNotFoundException ex = new StatusNotFoundException("DONE");

        // Assert
        assertTrue(ex.getMessage().contains("DONE"));
    }

    @Test
    void userNotValidExceptionPropagaMensaje() {
        // Arrange - Act
        UserNotValidException ex = new UserNotValidException("user no está en el hogar");

        // Assert
        assertEquals("user no está en el hogar", ex.getMessage());
    }
}
