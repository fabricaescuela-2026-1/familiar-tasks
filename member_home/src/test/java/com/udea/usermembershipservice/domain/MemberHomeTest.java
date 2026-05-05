package com.udea.usermembershipservice.domain;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;
import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.domain.model.MemberHome;
import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberHomeTest {

    private Home home;
    private Role role;
    private Person person;

    @BeforeEach
    void setUp() {
        home   = Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
        role   = Role.create(UUID.randomUUID(), "Administrador");
        person = Person.restore(UUID.randomUUID(), "Ana", "López", "ana@mail.com", "Segura@123", LocalDateTime.now(), true);
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void creacionExitosaConDatosValidos() {
        // Arrange - Act
        MemberHome memberHome = MemberHome.create(home, role, person);

        // Assert
        assertNotNull(memberHome);
        assertEquals(home, memberHome.getHome());
        assertEquals(role, memberHome.getRole());
        assertEquals(person, memberHome.getPerson());
    }

    @Test
    void restoreCreaMemberHomeSinValidar() {
        // Arrange - Act
        MemberHome memberHome = MemberHome.restore(home, role, person);

        // Assert
        assertNotNull(memberHome);
        assertEquals(home, memberHome.getHome());
    }

    @Test
    void cambiarHogarActualizaCorrectamente() {
        // Arrange
        MemberHome memberHome = MemberHome.create(home, role, person);
        Home nuevoHogar = Home.create(UUID.randomUUID(), "Los Ruiz", LocalDateTime.now());

        // Act
        memberHome.changeHome(nuevoHogar);

        // Assert
        assertEquals(nuevoHogar, memberHome.getHome());
    }

    @Test
    void cambiarRolActualizaCorrectamente() {
        // Arrange
        MemberHome memberHome = MemberHome.create(home, role, person);
        Role nuevoRol = Role.create(UUID.randomUUID(), "Miembro");

        // Act
        memberHome.changeRole(nuevoRol);

        // Assert
        assertEquals(nuevoRol, memberHome.getRole());
    }

    @Test
    void cambiarPersonaActualizaCorrectamente() {
        // Arrange
        MemberHome memberHome = MemberHome.create(home, role, person);
        Person nuevaPersona = Person.restore(UUID.randomUUID(), "Carlos", "Díaz", "carlos@mail.com", "Segura@123", LocalDateTime.now(), true);

        // Act
        memberHome.changePerson(nuevaPersona);

        // Assert
        assertEquals(nuevaPersona, memberHome.getPerson());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void crearConHogarNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            MemberHome.create(null, role, person)
        );
    }

    @Test
    void crearConRolNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            MemberHome.create(home, null, person)
        );
    }

    @Test
    void crearConPersonaNulaLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            MemberHome.create(home, role, null)
        );
    }

    @Test
    void cambiarHogarNuloLanzaExcepcion() {
        // Arrange
        MemberHome memberHome = MemberHome.create(home, role, person);

        // Act - Assert
        assertThrows(InvalidDataException.class, () ->
            memberHome.changeHome(null)
        );
    }

    @Test
    void cambiarRolNuloLanzaExcepcion() {
        // Arrange
        MemberHome memberHome = MemberHome.create(home, role, person);

        // Act - Assert
        assertThrows(InvalidDataException.class, () ->
            memberHome.changeRole(null)
        );
    }

    @Test
    void cambiarPersonaNulaLanzaExcepcion() {
        // Arrange
        MemberHome memberHome = MemberHome.create(home, role, person);

        // Act - Assert
        assertThrows(InvalidDataException.class, () ->
            memberHome.changePerson(null)
        );
    }
}
