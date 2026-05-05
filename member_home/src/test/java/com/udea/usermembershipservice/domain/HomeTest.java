package com.udea.usermembershipservice.domain;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;
import com.udea.usermembershipservice.domain.model.Home;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HomeTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void creacionExitosaConDatosValidos() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        // Act
        Home home = Home.create(id, "Casa Familiar", ahora);

        // Assert
        assertEquals(id, home.getIdHome());
        assertEquals("Casa Familiar", home.getName());
        assertEquals(ahora, home.getCreatedAt());
    }

    @Test
    void cambiarNombreExitoso() {
        // Arrange
        Home home = Home.create(UUID.randomUUID(), "Casa Vieja", LocalDateTime.now());

        // Act
        home.changeName("Casa Nueva");

        // Assert
        assertEquals("Casa Nueva", home.getName());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void idNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Home.create(null, "Casa Familiar", LocalDateTime.now())
        );
    }

    @Test
    void nombreNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Home.create(UUID.randomUUID(), null, LocalDateTime.now())
        );
    }

    @Test
    void nombreVacioLanzaExcepcion() {
        // Arrange
        String nombreVacio = "  ";

        // Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Home.create(UUID.randomUUID(), nombreVacio, LocalDateTime.now())
        );
    }

    @Test
    void fechaNulaLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Home.create(UUID.randomUUID(), "Casa Familiar", null)
        );
    }

    @Test
    void cambiarNombreVacioLanzaExcepcion() {
        // Arrange
        Home home = Home.create(UUID.randomUUID(), "Casa Familiar", LocalDateTime.now());

        // Act - Assert
        assertThrows(InvalidDataException.class, () -> home.changeName(""));
    }
}
