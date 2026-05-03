package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.port.out.IPasswordEncoderPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreatedUserUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;
import com.udea.usermembershipservice.aplication.useCase.dto.person.CreatePersonDto;
import com.udea.usermembershipservice.aplication.useCase.dto.person.PersonDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatedUserUseCaseTest {

    @Mock private IPersonRepositoryPort personRepositoryPort;
    @Mock private IPasswordEncoderPort passwordEncoderPort;
    @Mock private ILoginUserCase loginUserCase;

    @InjectMocks
    private CreatedUserUseCase useCase;

    private Person personaExistente() {
        return Person.restore(
            UUID.randomUUID(), "Carlos", "Ruiz",
            "carlos@mail.com", "hashedPass", LocalDateTime.now(), true
        );
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    // HU01 Scenario 1 — registro exitoso con datos válidos
    @Test
    void registroExitosoConDatosValidos() {
        // Arrange
        when(personRepositoryPort.getUserByEmail("nuevo@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoderPort.encode("Segura@123")).thenReturn("Segura@123");

        // Act - Assert
        assertDoesNotThrow(() ->
            useCase.createdUser(new CreatePersonDto("Carlos", "Ruiz", "nuevo@mail.com", "Segura@123"))
        );
        verify(personRepositoryPort).saveUser(any(Person.class));
    }

    @Test
    void obtenerTodosLosUsuariosRetornaLista() {
        // Arrange
        Person person = personaExistente();
        when(personRepositoryPort.getAllUsers()).thenReturn(List.of(person));

        // Act
        List<PersonDto> result = useCase.geatAllUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("carlos@mail.com", result.get(0).email());
    }

    @Test
    void obtenerUsuarioPorEmailExistenteRetornaDto() {
        // Arrange
        Person person = personaExistente();
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));

        // Act
        PersonDto result = useCase.getUserByEmail("carlos@mail.com");

        // Assert
        assertEquals("carlos@mail.com", result.email());
        assertEquals("Carlos", result.name());
    }

    @Test
    void eliminarUsuarioConLoginValidoEliminaCorrectamente() {
        // Arrange
        when(loginUserCase.login(any(LoginDto.class))).thenReturn(new LoginResultDto(true, "ok"));

        // Act - Assert
        assertDoesNotThrow(() ->
            useCase.deleteUser(new LoginDto("carlos@mail.com", "Segura@123"))
        );
        verify(personRepositoryPort).deleteUser("carlos@mail.com");
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    // HU01 Scenario 2 — correo ya registrado
    @Test
    void registroConCorreoDuplicadoLanzaExcepcion() {
        // Arrange
        Person person = personaExistente();
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.createdUser(new CreatePersonDto("Otro", "Usuario", "carlos@mail.com", "Segura@123"))
        );
        verify(personRepositoryPort, never()).saveUser(any());
    }

    @Test
    void obtenerUsuarioPorEmailInexistenteLanzaExcepcion() {
        // Arrange
        when(personRepositoryPort.getUserByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(SearchException.class, () ->
            useCase.getUserByEmail("noexiste@mail.com")
        );
    }

    @Test
    void eliminarUsuarioConLoginInvalidoLanzaExcepcion() {
        // Arrange
        when(loginUserCase.login(any(LoginDto.class))).thenReturn(new LoginResultDto(false, "invalid"));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.deleteUser(new LoginDto("carlos@mail.com", "incorrecta"))
        );
        verify(personRepositoryPort, never()).deleteUser(any());
    }
}
