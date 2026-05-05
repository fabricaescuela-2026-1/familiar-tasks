package com.udea.usermembershipservice.domain;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;
import com.udea.usermembershipservice.domain.model.Role;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void creacionExitosaConDatosValidos() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Role role = Role.create(id, "Administrador");

        // Assert
        assertEquals(id, role.getIdRole());
        assertEquals("Administrador", role.getName());
    }

    @Test
    void cambiarNombreExitoso() {
        // Arrange
        Role role = Role.create(UUID.randomUUID(), "Invitado");

        // Act
        role.changeName("Miembro");

        // Assert
        assertEquals("Miembro", role.getName());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void idNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Role.create(null, "Administrador")
        );
    }

    @Test
    void nombreNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Role.create(UUID.randomUUID(), null)
        );
    }

    @Test
    void nombreVacioLanzaExcepcion() {
        // Arrange
        String nombreVacio = "   ";

        // Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Role.create(UUID.randomUUID(), nombreVacio)
        );
    }

    @Test
    void cambiarNombreVacioLanzaExcepcion() {
        // Arrange
        Role role = Role.create(UUID.randomUUID(), "Invitado");

        // Act - Assert
        assertThrows(InvalidDataException.class, () -> role.changeName(""));
    }
}
