package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.UserSynchronizationService;
import com.udea.usermembershipservice.aplication.useCase.dto.queue.UserRegistrationEvent;
import com.udea.usermembershipservice.domain.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSynchronizationServiceTest {

    @Mock private IPersonRepositoryPort personRepositoryPort;

    @InjectMocks
    private UserSynchronizationService service;

    private UserRegistrationEvent eventoValido(String userId) {
        return new UserRegistrationEvent(
            userId, "Carlos", "Ruiz", "carlos@mail.com",
            "hashed-pass", "2025-01-15T10:30:00"
        );
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void sincronizacionExitosaCuandoUsuarioNoExiste() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        when(personRepositoryPort.getUserById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        service.synchronizeUser(eventoValido(userId));

        // Assert
        verify(personRepositoryPort).saveUser(any(Person.class));
    }

    // ── CASOS DE NO ACCIÓN ──────────────────────────────────────────────────

    @Test
    void sincronizacionOmitidaSiUsuarioYaExiste() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        Person personaExistente = Person.restore(
            UUID.fromString(userId), "Carlos", "Ruiz",
            "carlos@mail.com", "hashed", java.time.LocalDateTime.of(2026, 1, 1, 10, 0, 0), true
        );
        when(personRepositoryPort.getUserById(any(UUID.class))).thenReturn(Optional.of(personaExistente));

        // Act
        service.synchronizeUser(eventoValido(userId));

        // Assert
        verify(personRepositoryPort, never()).saveUser(any());
    }

    @Test
    void sincronizacionConUUIDInvalidoNoLanzaExcepcion() {
        // Arrange
        var eventoInvalido = new UserRegistrationEvent(
            "no-es-un-uuid", "Carlos", "Ruiz",
            "carlos@mail.com", "hashed", "2025-01-15T10:30:00"
        );

        // Act - Assert
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.synchronizeUser(eventoInvalido));
        verify(personRepositoryPort, never()).saveUser(any());
    }

    @Test
    void sincronizacionConFechaInvalidaNoLanzaExcepcion() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        var eventoFechaInvalida = new UserRegistrationEvent(
            userId, "Carlos", "Ruiz",
            "carlos@mail.com", "hashed", "fecha-no-valida"
        );
        when(personRepositoryPort.getUserById(any(UUID.class))).thenReturn(Optional.empty());

        // Act - Assert
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.synchronizeUser(eventoFechaInvalida));
        verify(personRepositoryPort, never()).saveUser(any());
    }
}
