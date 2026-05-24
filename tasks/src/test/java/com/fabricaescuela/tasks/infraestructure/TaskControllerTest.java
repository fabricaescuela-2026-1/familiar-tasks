package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.domain.ports.in.TaskUseCasePort;
import com.fabricaescuela.tasks.infraestructure.presentation.TaskController;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.RequestTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ResponseTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock private TaskUseCasePort service;

    @InjectMocks
    private TaskController controller;

    private RequestTask requestTareaValida() {
        return RequestTask.builder()
            .name("Lavar ropa")
            .description("Cargar lavadora")
            .statusName("PENDIENTE")
            .priorityName("MEDIA")
            .homeId(UUID.randomUUID())
            .guestId(UUID.randomUUID())
            .deadline(LocalDateTime.now().plusDays(1))
            .build();
    }

    private Task tareaDeDominio() {
        return Task.builder()
            .taskId(UUID.randomUUID())
            .name("Lavar ropa")
            .description("Cargar lavadora")
            .status("PENDIENTE")
            .priority("MEDIA")
            .homeId(UUID.randomUUID())
            .guestId(UUID.randomUUID())
            .deadline(LocalDateTime.now().plusDays(1))
            .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void createTaskRetorna200ConTareaCreada() {
        // Arrange
        RequestTask request = requestTareaValida();
        Task tarea = tareaDeDominio();
        when(service.create(any(Task.class))).thenReturn(tarea);

        // Act
        ResponseEntity<ResponseTask> resultado = controller.createTask(request);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(tarea.getTaskId(), resultado.getBody().getTaskId());
        verify(service).create(any(Task.class));
    }

    @Test
    void updateTaskRetorna200ConTareaActualizada() {
        // Arrange
        UUID id = UUID.randomUUID();
        Task actualizada = tareaDeDominio();
        when(service.update(eq(id), any(Task.class))).thenReturn(actualizada);

        // Act
        ResponseEntity<ResponseTask> resultado = controller.updateTask(id, requestTareaValida());

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        verify(service).update(eq(id), any(Task.class));
    }

    @Test
    void deleteTaskRetorna204() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        ResponseEntity<Void> resultado = controller.deleteTask(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());
        verify(service).delete(id);
    }

    @Test
    void findAllRetorna200ConListaDeTareas() {
        // Arrange
        when(service.findAll()).thenReturn(List.of(tareaDeDominio(), tareaDeDominio()));

        // Act
        ResponseEntity<List<ResponseTask>> resultado = controller.findAll();

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(2, resultado.getBody().size());
    }
}
