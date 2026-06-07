package com.fabricaescuela.tasks.infraestructure.database;

import com.fabricaescuela.tasks.application.dto.TaskSearchCriteria;
import com.fabricaescuela.tasks.domain.exceptions.PriorityNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.StatusNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.TaskNotFoundException;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.infraestructure.database.entyties.PriorityEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.StatusEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskRepositoryAdapterTest {

    @Mock private TaskJpaRepository taskRepository;
    @Mock private PriorityJpaRepository priorityRepository;
    @Mock private StatusJpaRepository statusRepository;

    @InjectMocks private TaskRepositoryAdapter adapter;

    private Task tareaValida() {
        return Task.builder()
            .name("Barrer")
            .description("Patio")
            .priority("MEDIA")
            .status("PENDIENTE")
            .deadline(LocalDateTime.now().plusDays(2))
            .homeId(UUID.randomUUID())
            .guestId(UUID.randomUUID())
            .build();
    }

    private TaskEntity entidadConPrioridadYStatus() {
        return TaskEntity.builder()
            .taskId(UUID.randomUUID())
            .name("Barrer")
            .description("Patio")
            .priority(PriorityEntity.builder().name("MEDIA").build())
            .status(StatusEntity.builder().name("PENDIENTE").build())
            .deadline(LocalDateTime.now().plusDays(2))
            .homeId(UUID.randomUUID())
            .guestId(UUID.randomUUID())
            .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void saveAsignaPrioridadStatusUuidYDelega() {
        // Arrange
        Task tarea = tareaValida();
        PriorityEntity prio = PriorityEntity.builder().name("MEDIA").build();
        StatusEntity stat = StatusEntity.builder().name("PENDIENTE").build();
        when(priorityRepository.findByName("MEDIA")).thenReturn(Optional.of(prio));
        when(statusRepository.findByName("PENDIENTE")).thenReturn(Optional.of(stat));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(inv -> {
            TaskEntity arg = inv.getArgument(0);
            return arg;
        });

        // Act
        Task result = adapter.save(tarea);

        // Assert
        assertNotNull(result.getTaskId());
        assertEquals("MEDIA", result.getPriority());
        assertEquals("PENDIENTE", result.getStatus());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void updateActualizaCamposYRetornaTareaActualizada() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        TaskEntity existing = entidadConPrioridadYStatus();
        existing.setTaskId(taskId);
        PriorityEntity prio = PriorityEntity.builder().name("MEDIA").build();
        StatusEntity stat = StatusEntity.builder().name("PENDIENTE").build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));
        when(priorityRepository.findByName("MEDIA")).thenReturn(Optional.of(prio));
        when(statusRepository.findByName("PENDIENTE")).thenReturn(Optional.of(stat));
        when(taskRepository.save(existing)).thenReturn(existing);

        // Act
        Task result = adapter.update(taskId, tarea);

        // Assert
        assertEquals(tarea.getName(), result.getName());
        verify(taskRepository).save(existing);
    }

    @Test
    void updateStatusCambiaEstadoYPersiste() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskEntity existing = entidadConPrioridadYStatus();
        existing.setTaskId(taskId);
        StatusEntity nuevo = StatusEntity.builder().name("EN_PROGRESO").build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));
        when(statusRepository.findByName("EN_PROGRESO")).thenReturn(Optional.of(nuevo));
        when(taskRepository.save(existing)).thenReturn(existing);

        // Act
        Task result = adapter.updateStatus(taskId, "EN_PROGRESO");

        // Assert
        assertEquals("EN_PROGRESO", result.getStatus());
    }

    @Test
    void findByIdMapeaEntidadADominio() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskEntity entity = entidadConPrioridadYStatus();
        entity.setTaskId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(entity));

        // Act
        Optional<Task> result = adapter.findById(taskId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get().getTaskId());
    }

    @Test
    void findByIdRetornaVacioCuandoNoExiste() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertTrue(adapter.findById(taskId).isEmpty());
    }

    @Test
    void deleteInvocaJpaCuandoExiste() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);
        adapter.delete(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void findAllMapeaListaDeEntidades() {
        TaskEntity e1 = entidadConPrioridadYStatus();
        TaskEntity e2 = entidadConPrioridadYStatus();
        when(taskRepository.findAll()).thenReturn(List.of(e1, e2));
        List<Task> result = adapter.findAll();
        assertEquals(2, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchAplicaSpecificationYMapea() {
        TaskEntity e1 = entidadConPrioridadYStatus();
        when(taskRepository.findAll(any(Specification.class))).thenReturn(List.of(e1));
        List<Task> result = adapter.search(new TaskSearchCriteria("barrer"));
        assertEquals(1, result.size());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void saveFallaCuandoPrioridadNoExiste() {
        Task tarea = tareaValida();
        when(priorityRepository.findByName("MEDIA")).thenReturn(Optional.empty());
        assertThrows(PriorityNotFoundException.class, () -> adapter.save(tarea));
    }

    @Test
    void saveFallaCuandoStatusNoExiste() {
        Task tarea = tareaValida();
        when(priorityRepository.findByName("MEDIA"))
            .thenReturn(Optional.of(PriorityEntity.builder().name("MEDIA").build()));
        when(statusRepository.findByName("PENDIENTE")).thenReturn(Optional.empty());
        assertThrows(StatusNotFoundException.class, () -> adapter.save(tarea));
    }

    @Test
    void updateFallaCuandoTareaNoExiste() {
        UUID taskId = UUID.randomUUID();
        Task tarea = tareaValida();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> adapter.update(taskId, tarea));
    }

    @Test
    void updateStatusFallaCuandoTareaNoExiste() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> adapter.updateStatus(taskId, "EN_PROGRESO"));
    }

    @Test
    void updateStatusFallaCuandoStatusNoExiste() {
        UUID taskId = UUID.randomUUID();
        TaskEntity existing = entidadConPrioridadYStatus();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));
        when(statusRepository.findByName("FOO")).thenReturn(Optional.empty());
        assertThrows(StatusNotFoundException.class, () -> adapter.updateStatus(taskId, "FOO"));
    }

    @Test
    void deleteFallaCuandoTareaNoExiste() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> adapter.delete(taskId));
        verify(taskRepository, never()).deleteById(any());
    }
}
