package com.udea.usermembershipservice.domain;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;
import com.udea.usermembershipservice.domain.exception.InvalidEmailException;
import com.udea.usermembershipservice.domain.exception.InvalidPasswordException;
import com.udea.usermembershipservice.domain.model.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private final LocalDateTime ahora = LocalDateTime.now();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void creacionExitosaConDatosValidos() {
        // Arrange
        String email = "Juan.Perez@Ejemplo.com";

        // Act
        Person person = Person.create("Juan", "Pérez", email, "segura123", ahora, true);

        // Assert
        assertNotNull(person.getIdPerson());
        assertEquals("juan.perez@ejemplo.com", person.getEmail());
        assertEquals("Juan", person.getName());
    }

    @Test
    void emailSeNormalizaEnMinusculas() {
        // Arrange
        String emailConMayusculas = "USUARIO@DOMINIO.COM";

        // Act
        Person person = Person.create("Ana", "López", emailConMayusculas, "clave1234", ahora, true);

        // Assert
        assertEquals("usuario@dominio.com", person.getEmail());
    }

    // Valor límite: exactamente 8 caracteres con dígito es el mínimo válido en create
    @Test
    void contrasenaConOchoCaracteresYDigitoEsValida() {
        // Arrange
        String contrasena8 = "clave123";

        // Act
        Person person = Person.create("Ana", "López", "ana@mail.com", contrasena8, ahora, true);

        // Assert
        assertNotNull(person);
    }

    @Test
    void cambiarEmailNormalizaAMinusculas() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "clave1234", ahora, true);

        // Act
        person.changeEmail("NUEVO@CORREO.COM");

        // Assert
        assertEquals("nuevo@correo.com", person.getEmail());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void nombreNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create(null, "López", "ana@mail.com", "clave1234", ahora, true)
        );
    }

    @Test
    void nombreVacioLanzaExcepcion() {
        // Arrange
        String nombreVacio = "   ";

        // Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create(nombreVacio, "López", "ana@mail.com", "clave1234", ahora, true)
        );
    }

    @Test
    void apellidoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create("Ana", null, "ana@mail.com", "clave1234", ahora, true)
        );
    }

    @Test
    void emailNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidEmailException.class, () ->
            Person.create("Ana", "López", null, "clave1234", ahora, true)
        );
    }

    @Test
    void emailSinArrobaLanzaExcepcion() {
        // Arrange
        String emailMalFormado = "usuariosindominio";

        // Act - Assert
        assertThrows(InvalidEmailException.class, () ->
            Person.create("Ana", "López", emailMalFormado, "clave1234", ahora, true)
        );
    }

    // Valor límite: 7 caracteres es inválido (mínimo es 8 en create)
    @Test
    void contrasenaConSieteCaracteresEsInvalida() {
        // Arrange
        String contrasena7 = "clave12";

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", contrasena7, ahora, true)
        );
    }

    @Test
    void contrasenaSinDigitoLanzaExcepcion() {
        // Arrange
        String sinDigito = "contrasena";

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", sinDigito, ahora, true)
        );
    }

    @Test
    void fechaCreacionNulaLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", "clave1234", null, true)
        );
    }

    @Test
    void activoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", "clave1234", ahora, null)
        );
    }

    @Test
    void cambiarEmailInvalidoLanzaExcepcion() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "clave1234", ahora, true);

        // Act - Assert
        assertThrows(InvalidEmailException.class, () -> person.changeEmail("noesuncorreo"));
    }

    @Test
    void cambiarNombreVacioLanzaExcepcion() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "clave1234", ahora, true);

        // Act - Assert
        assertThrows(InvalidDataException.class, () -> person.changeName(""));
    }

    // Valor límite: changePassword requiere mínimo 6 caracteres (diferente a create que requiere 8)
    @Test
    void cambiarContrasenaConCincoCaracteresEsInvalida() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "clave1234", ahora, true);

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () -> person.changePassword("cl1ab"));
    }

    @Test
    void cambiarContrasenaSinDigitoLanzaExcepcion() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "clave1234", ahora, true);

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () -> person.changePassword("sindigito"));
    }

    // GAP HU02: la HU exige al menos un carácter especial, pero el código solo valida dígitos.
    // Una contraseña sin carácter especial pasa la validación actual.
    @Test
    void contrasenaSinCaracterEspecialEsAceptadaPorElCodigo() {
        // Arrange
        String sinEspecial = "sincaracter1";

        // Act
        Person person = Person.create("Ana", "López", "ana@mail.com", sinEspecial, ahora, true);

        // Assert — el código la acepta; per HU02 debería rechazarla
        assertNotNull(person);
    }
}
