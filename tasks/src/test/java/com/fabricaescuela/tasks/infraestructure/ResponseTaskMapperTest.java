package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ResponseTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers.ResponseTaskMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTaskMapperTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toResponseConvierteDominioADto() {
        // Arrange
        UUID taskId  = UUID.randomUUID();
        UUID homeId  = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        LocalDateTime ahora   = LocalDateTime.now();
        LocalDateTime deadline = ahora.plusDays(5);

        Task task = Task.builder()
            .taskId(taskId)
            .name("Limpiar cocina")
            .description("Limpiar mesones y estufa")
            .status("PENDIENTE")
            .priority("MEDIA")
            .homeId(homeId)
            .guestId(guestId)
            .createdAt(ahora)
            .deadline(deadline)
            .build();

        // Act
        ResponseTask response = ResponseTaskMapper.toResponse(task);

        // Assert
        assertEquals(taskId,           response.getTaskId());
        assertEquals("Limpiar cocina", response.getName());
        assertEquals("Limpiar mesones y estufa", response.getDescription());
        assertEquals("PENDIENTE",      response.getStatusName());
        assertEquals("MEDIA",          response.getPriorityName());
        assertEquals(homeId,           response.getHomeId());
        assertEquals(guestId,          response.getGuestId());
        assertEquals(ahora,            response.getCreatedAt());
        assertEquals(deadline,         response.getDeadline());
    }

    @Test
    void toResponseConTareaVaciaNoLanzaExcepcion() {
        // Arrange
        Task task = Task.builder().build();

        // Act - Assert
        assertDoesNotThrow(() -> ResponseTaskMapper.toResponse(task));
    }
}
