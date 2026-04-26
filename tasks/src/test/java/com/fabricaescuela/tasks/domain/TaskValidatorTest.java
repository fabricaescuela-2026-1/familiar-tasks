package com.fabricaescuela.tasks.domain;

import com.fabricaescuela.tasks.domain.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskValidatorTest {

    private Task tareaValida() {
        return Task.builder()
                .name("Limpiar cocina")
                .description("Limpiar mesones y lavar platos")
                .status("PENDIENTE")
                .priority("ALTA")
                .deadline(LocalDateTime.now().plusDays(3))
                .homeId(UUID.randomUUID())
                .guestId(UUID.randomUUID())
                .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void tareaCompletaNoLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();

        // Act - Assert
        assertDoesNotThrow(() -> TaskValidator.validate(tarea));
    }

    // Valor límite: deadline mañana es el caso más cercano al presente que es válido
    @Test
    void deadlineMananaEsValido() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setDeadline(LocalDateTime.now().plusDays(1));

        // Act - Assert
        assertDoesNotThrow(() -> TaskValidator.validate(tarea));
    }

    @Test
    void guestIdValidoNoLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();

        // Act - Assert
        assertDoesNotThrow(() -> TaskValidator.validateUserIds(tarea));
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void nombreNuloLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setName(null);

        // Act - Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> TaskValidator.validate(tarea));
        assertEquals("Name is required", ex.getMessage());
    }

    @Test
    void nombreEnBlancoLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setName("   ");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> TaskValidator.validate(tarea));
    }

    @Test
    void descripcionNulaLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setDescription(null);

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> TaskValidator.validate(tarea));
    }

    @Test
    void estadoNuloLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setStatus(null);

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> TaskValidator.validate(tarea));
    }

    @Test
    void prioridadNulaLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setPriority(null);

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> TaskValidator.validate(tarea));
    }

    @Test
    void deadlineNuloLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setDeadline(null);

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> TaskValidator.validate(tarea));
    }

    // Valor límite: un segundo en el pasado ya es inválido
    @Test
    void deadlineEnElPasadoLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setDeadline(LocalDateTime.now().minusSeconds(1));

        // Act - Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> TaskValidator.validate(tarea));
        assertEquals("Deadline must be in the future", ex.getMessage());
    }

    @Test
    void homeIdNuloLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setHomeId(null);

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> TaskValidator.validate(tarea));
    }

    @Test
    void guestIdNuloLanzaExcepcion() {
        // Arrange
        Task tarea = tareaValida();
        tarea.setGuestId(null);

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> TaskValidator.validateUserIds(tarea));
    }
}
