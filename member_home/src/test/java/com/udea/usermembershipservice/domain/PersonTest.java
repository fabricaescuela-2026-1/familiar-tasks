package com.udea.usermembershipservice.domain;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;
import com.udea.usermembershipservice.domain.model.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private final UUID id = UUID.randomUUID();
    private final LocalDateTime ahora = LocalDateTime.now();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void restauracionExitosaConDatosValidos() {
        // Arrange - Act
        Person person = Person.restore(id, "Juan", "Pérez", "Juan.Perez@Ejemplo.com", "hashed", ahora, true);

        // Assert
        assertEquals(id, person.getIdPerson());
        assertEquals("Juan", person.getName());
        assertEquals("Pérez", person.getLastName());
        assertEquals("juan.perez@ejemplo.com", person.getEmail());
        assertEquals("hashed", person.getPasswordHash());
        assertEquals(ahora, person.getCreatedAt());
        assertTrue(person.getActive());
    }

    @Test
    void emailSeNormalizaEnMinusculas() {
        // Arrange - Act
        Person person = Person.restore(id, "Ana", "López", "USUARIO@DOMINIO.COM", "hashed", ahora, true);

        // Assert
        assertEquals("usuario@dominio.com", person.getEmail());
    }

    @Test
    void personaInactivaEsValida() {
        // Arrange - Act
        Person person = Person.restore(id, "Ana", "López", "ana@mail.com", "hashed", ahora, false);

        // Assert
        assertFalse(person.getActive());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void idNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(null, "Ana", "López", "ana@mail.com", "hashed", ahora, true)
        );
    }

    @Test
    void nombreNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, null, "López", "ana@mail.com", "hashed", ahora, true)
        );
    }

    @Test
    void nombreVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, "   ", "López", "ana@mail.com", "hashed", ahora, true)
        );
    }

    @Test
    void apellidoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, "Ana", null, "ana@mail.com", "hashed", ahora, true)
        );
    }

    @Test
    void apellidoVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, "Ana", "   ", "ana@mail.com", "hashed", ahora, true)
        );
    }

    @Test
    void emailNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, "Ana", "López", null, "hashed", ahora, true)
        );
    }

    @Test
    void emailVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, "Ana", "López", "   ", "hashed", ahora, true)
        );
    }

    @Test
    void fechaCreacionNulaLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, "Ana", "López", "ana@mail.com", "hashed", null, true)
        );
    }

    @Test
    void activoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.restore(id, "Ana", "López", "ana@mail.com", "hashed", ahora, null)
        );
    }
}
