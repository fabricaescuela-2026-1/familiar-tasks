package com.fabrica.authentication.domain;

import com.fabrica.authentication.application.dto.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataValidatorTest {

    private final DataValidator validator = new DataValidator();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void validateNewUserRequestPasaConDatosValidosYContrasenaFuerte() {
        // Arrange
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "ana@mail.com", "Segura@123", "Segura@123");

        // Act - Assert
        assertDoesNotThrow(() -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestPasaCuandoConfirmacionEsNull() {
        // Arrange
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "ana@mail.com", "Segura@123");

        // Act - Assert
        assertDoesNotThrow(() -> validator.validateNewUserRequest(req));
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void validateNewUserRequestFallaCuandoNombreEsNull() {
        RegisterRequest req = new RegisterRequest(null, "Perez", "a@mail.com", "Segura@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoNombreEsVacio() {
        RegisterRequest req = new RegisterRequest("  ", "Perez", "a@mail.com", "Segura@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoApellidoEsNull() {
        RegisterRequest req = new RegisterRequest("Ana", null, "a@mail.com", "Segura@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoApellidoEsVacio() {
        RegisterRequest req = new RegisterRequest("Ana", "  ", "a@mail.com", "Segura@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoEmailEsNull() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", null, "Segura@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoEmailEsVacio() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "   ", "Segura@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoPasswordEsNull() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", null);
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoPasswordEsBlank() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", "   ");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    // valor límite: 7 caracteres está justo por debajo del mínimo de 8
    @Test
    void validateNewUserRequestFallaCuandoPasswordTieneMenosDeOchoCaracteres() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", "Aa1@xyz");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoPasswordNoTieneMayuscula() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", "segura@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoPasswordNoTieneMinuscula() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", "SEGURA@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoPasswordNoTieneDigito() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", "Segura@abc");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoPasswordNoTieneCaracterEspecial() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", "Segura123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }

    @Test
    void validateNewUserRequestFallaCuandoConfirmacionNoCoincide() {
        RegisterRequest req = new RegisterRequest("Ana", "Perez", "a@mail.com", "Segura@123", "Otra@123");
        assertThrows(IllegalArgumentException.class, () -> validator.validateNewUserRequest(req));
    }
}
