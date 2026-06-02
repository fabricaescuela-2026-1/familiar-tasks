package com.fabricaescuela.tasks.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fabricaescuela.tasks.application.dto.UserRegistrationEvent;
import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;
import com.fabricaescuela.tasks.infraestructure.database.jpa.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserSynchronizationServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserSynchronizationService service;

  private UserRegistrationEvent eventoValido(String userId) {
    return new UserRegistrationEvent(
        userId, "Carlos", "Ruiz", "carlos@mail.com",
        "hashed-pass", "2025-01-15T10:30:00");
  }

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void sincronizaUsuarioNuevoLoGuardaEnElRepositorio() {
    // Arrange
    String userId = UUID.randomUUID().toString();
    UserRegistrationEvent event = eventoValido(userId);
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // Act
    service.synchronizeUser(event);

    // Assert
    verify(userRepository, times(1)).save(any(UserEntity.class));
  }

  @Test
  void cuandoElUsuarioYaExisteNoLoGuardaDeNuevo() {
    // Arrange
    String userId = UUID.randomUUID().toString();
    UserRegistrationEvent event = eventoValido(userId);
    when(userRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(UserEntity.builder().build()));

    // Act
    service.synchronizeUser(event);

    // Assert
    verify(userRepository, never()).save(any(UserEntity.class));
  }

  // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

  @Test
  void userIdInvalidoNoLanzaExcepcionYNoGuarda() {
    // Arrange
    UserRegistrationEvent event = eventoValido("not-a-uuid");

    // Act
    service.synchronizeUser(event);

    // Assert
    verify(userRepository, never()).save(any(UserEntity.class));
  }

  @Test
  void fechaCreacionInvalidaNoGuardaUsuario() {
    // Arrange
    String userId = UUID.randomUUID().toString();
    UserRegistrationEvent event = new UserRegistrationEvent(
        userId, "Carlos", "Ruiz", "carlos@mail.com",
        "hashed-pass", "fecha-invalida");
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // Act
    service.synchronizeUser(event);

    // Assert
    verify(userRepository, never()).save(any(UserEntity.class));
  }
}
