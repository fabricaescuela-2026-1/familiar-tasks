package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.RequestTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers.RequestTaskMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestTaskMapperTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toDomainConvierteDtoADominio() {
        // Arrange
        UUID homeId  = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        LocalDateTime deadline = LocalDateTime.now().plusDays(3);

        RequestTask request = RequestTask.builder()
            .name("Barrer patio")
            .description("Barrer las hojas")
            .statusName("PENDIENTE")
            .priorityName("ALTA")
            .homeId(homeId)
            .guestId(guestId)
            .deadline(deadline)
            .build();

        // Act
        Task task = RequestTaskMapper.toDomain(request);

        // Assert
        assertEquals("Barrer patio",  task.getName());
        assertEquals("Barrer las hojas", task.getDescription());
        assertEquals("PENDIENTE",     task.getStatus());
        assertEquals("ALTA",          task.getPriority());
        assertEquals(homeId,          task.getHomeId());
        assertEquals(guestId,         task.getGuestId());
        assertEquals(deadline,        task.getDeadline());
    }

    @Test
    void toDomainConCamposNulosNoLanzaExcepcion() {
        // Arrange
        RequestTask request = RequestTask.builder().build();

        // Act - Assert
        assertDoesNotThrow(() -> RequestTaskMapper.toDomain(request));
    }
}
