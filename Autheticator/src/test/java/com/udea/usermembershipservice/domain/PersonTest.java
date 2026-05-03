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
        Person person = Person.create("Juan", "Pérez", email, "Segura@123", ahora, true);

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
        Person person = Person.create("Ana", "López", emailConMayusculas, "Segura@123", ahora, true);

        // Assert
        assertEquals("usuario@dominio.com", person.getEmail());
    }

    // HU02 Scenario 1 — valor límite: más de 8 chars, mayúsculas y caracteres especiales
    @Test
    void contrasenaConMayusculasYCaracterEspecialEsAceptada() {
        // Arrange
        String contrasenaSegura = "Segura@123";

        // Act
        Person person = Person.create("Ana", "López", "ana@mail.com", contrasenaSegura, ahora, true);

        // Assert
        assertNotNull(person);
    }

    @Test
    void cambiarEmailNormalizaAMinusculas() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "Segura@123", ahora, true);

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
            Person.create(null, "López", "ana@mail.com", "Segura@123", ahora, true)
        );
    }

    @Test
    void nombreVacioLanzaExcepcion() {
        // Arrange
        String nombreVacio = "   ";

        // Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create(nombreVacio, "López", "ana@mail.com", "Segura@123", ahora, true)
        );
    }

    @Test
    void apellidoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create("Ana", null, "ana@mail.com", "Segura@123", ahora, true)
        );
    }

    @Test
    void emailNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidEmailException.class, () ->
            Person.create("Ana", "López", null, "Segura@123", ahora, true)
        );
    }

    @Test
    void emailSinArrobaLanzaExcepcion() {
        // Arrange
        String emailMalFormado = "usuariosindominio";

        // Act - Assert
        assertThrows(InvalidEmailException.class, () ->
            Person.create("Ana", "López", emailMalFormado, "Segura@123", ahora, true)
        );
    }

    @Test
    void fechaCreacionNulaLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", "Segura@123", null, true)
        );
    }

    @Test
    void activoNuloLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", "Segura@123", ahora, null)
        );
    }

    // HU02 Scenario 2 — valor límite: 7 chars es inválido, mínimo es 8
    @Test
    void contrasenaConSieteCaracteresEsInvalida() {
        // Arrange
        String contrasena7 = "Abc@123";

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", contrasena7, ahora, true)
        );
    }

    // HU02 Scenario 3 — sin caracteres especiales debe ser rechazada
    @Test
    void contrasenaSinCaracterEspecialEsRechazada() {
        // Arrange
        String sinEspecial = "sinEspecial8";

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", sinEspecial, ahora, true)
        );
    }

    // HU02 Scenario 1 — la HU exige mayúsculas; contraseña sin mayúscula debe ser rechazada
    @Test
    void contrasenaSinMayusculaEsRechazada() {
        // Arrange
        String sinMayuscula = "segura@123";

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () ->
            Person.create("Ana", "López", "ana@mail.com", sinMayuscula, ahora, true)
        );
    }

    // HU01 Scenario 4 — apellido vacío no debe ser aceptado
    @Test
    void apellidoVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        assertThrows(InvalidDataException.class, () ->
            Person.create("Ana", "   ", "ana@mail.com", "Segura@123", ahora, true)
        );
    }

    @Test
    void cambiarEmailInvalidoLanzaExcepcion() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "Segura@123", ahora, true);

        // Act - Assert
        assertThrows(InvalidEmailException.class, () -> person.changeEmail("noesuncorreo"));
    }

    @Test
    void cambiarNombreVacioLanzaExcepcion() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "Segura@123", ahora, true);

        // Act - Assert
        assertThrows(InvalidDataException.class, () -> person.changeName(""));
    }

    // HU02 Scenario 2 — valor límite en changePassword: 7 chars es inválido según HU
    @Test
    void cambiarContrasenaCon7CaracteresEsRechazada() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "Segura@123", ahora, true);
        String contrasena7 = "Abc@123";

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () -> person.changePassword(contrasena7));
    }

    // HU02 Scenario 3 — sin caracteres especiales debe ser rechazada en changePassword
    @Test
    void cambiarContrasenaSinCaracterEspecialEsRechazada() {
        // Arrange
        Person person = Person.create("Ana", "López", "ana@mail.com", "Segura@123", ahora, true);
        String sinEspecial = "sinEspecial8";

        // Act - Assert
        assertThrows(InvalidPasswordException.class, () -> person.changePassword(sinEspecial));
    }
}
