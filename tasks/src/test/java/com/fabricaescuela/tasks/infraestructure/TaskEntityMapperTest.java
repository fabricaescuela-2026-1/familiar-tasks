package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.database.entyties.PriorityEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.StatusEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;
import com.fabricaescuela.tasks.infraestructure.database.mappers.TaskEntityMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskEntityMapperTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toDomainConvierteEntidadADominio() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID homeId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        LocalDateTime deadline = LocalDateTime.now().plusDays(2);
        StatusEntity status = StatusEntity.builder().statusId(UUID.randomUUID()).name("PENDIENTE").build();
        PriorityEntity priority = PriorityEntity.builder().priorityId(UUID.randomUUID()).name("ALTA").build();
        TaskEntity entity = TaskEntity.builder()
            .taskId(taskId)
            .name("Limpiar")
            .description("Limpiar la sala")
            .status(status)
            .priority(priority)
            .homeId(homeId)
            .guestId(guestId)
            .deadline(deadline)
            .build();

        // Act
        Task task = TaskEntityMapper.toDomain(entity);

        // Assert
        assertEquals(taskId,       task.getTaskId());
        assertEquals("Limpiar",    task.getName());
        assertEquals("Limpiar la sala", task.getDescription());
        assertEquals("PENDIENTE",  task.getStatus());
        assertEquals("ALTA",       task.getPriority());
        assertEquals(homeId,       task.getHomeId());
        assertEquals(guestId,      task.getGuestId());
        assertEquals(deadline,     task.getDeadline());
    }

    @Test
    void toEntityConvierteDominioAEntidad() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UUID homeId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        LocalDateTime deadline = LocalDateTime.now().plusDays(3);
        Task task = Task.builder()
            .taskId(taskId)
            .name("Cocinar")
            .description("Almuerzo")
            .status("PENDIENTE")
            .priority("MEDIA")
            .homeId(homeId)
            .guestId(guestId)
            .deadline(deadline)
            .build();

        // Act
        TaskEntity entity = TaskEntityMapper.toEntity(task);

        // Assert
        assertEquals(taskId,    entity.getTaskId());
        assertEquals("Cocinar", entity.getName());
        assertEquals("Almuerzo", entity.getDescription());
        assertEquals(homeId,    entity.getHomeId());
        assertEquals(guestId,   entity.getGuestId());
        assertEquals(deadline,  entity.getDeadline());
    }
}
