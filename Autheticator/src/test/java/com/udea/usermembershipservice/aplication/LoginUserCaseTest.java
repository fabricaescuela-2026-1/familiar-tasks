package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.out.IPasswordEncoderPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.LoginUserCase;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;
import com.udea.usermembershipservice.aplication.useCase.exception.LoginException;
import com.udea.usermembershipservice.domain.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserCaseTest {

    @Mock private IPersonRepositoryPort personRepositoryPort;
    @Mock private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private LoginUserCase useCase;

    private Person personaRegistrada() {
        return Person.restore(
            UUID.randomUUID(), "Carlos", "Ruiz",
            "carlos@mail.com", "hashedPass", LocalDateTime.now(), true
        );
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    // HU01 Scenario 1 — login exitoso con credenciales válidas
    @Test
    void loginExitosoConCredencialesValidas() {
        // Arrange
        Person person = personaRegistrada();
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));
        when(passwordEncoderPort.matches("Segura@123", "hashedPass")).thenReturn(true);

        // Act
        LoginResultDto result = useCase.login(new LoginDto("carlos@mail.com", "Segura@123"));

        // Assert
        assertTrue(result.acces());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    // HU01 Scenario 2 — correo no registrado
    @Test
    void loginConCorreoNoRegistradoLanzaExcepcion() {
        // Arrange
        when(personRepositoryPort.getUserByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(LoginException.class, () ->
            useCase.login(new LoginDto("noexiste@mail.com", "Segura@123"))
        );
    }

    // HU01 — contraseña incorrecta retorna acceso denegado
    @Test
    void loginConContrasenaIncorrectaRetornaAccesoDenegado() {
        // Arrange
        Person person = personaRegistrada();
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));
        when(passwordEncoderPort.matches("incorrecta", "hashedPass")).thenReturn(false);

        // Act
        LoginResultDto result = useCase.login(new LoginDto("carlos@mail.com", "incorrecta"));

        // Assert
        assertFalse(result.acces());
    }
}
