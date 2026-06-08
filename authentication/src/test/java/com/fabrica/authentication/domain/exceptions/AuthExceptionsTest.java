package com.fabrica.authentication.domain.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthExceptionsTest {

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void emailSendingExceptionRetornaMensaje() {
    // Arrange - Act
    EmailSendingException ex = new EmailSendingException("error envio");

    // Assert
    assertEquals("error envio", ex.getMessage());
  }

  @Test
  void invalidTwoFactorAuthTokenExceptionRetornaMensaje() {
    // Arrange - Act
    InvalidTwoFactorAuthTokenException ex =
      new InvalidTwoFactorAuthTokenException("codigo invalido");

    // Assert
    assertEquals("codigo invalido", ex.getMessage());
  }

  @Test
  void inactiveAccountExceptionTieneMensajeNoNulo() {
    // Arrange - Act
    InactiveAccountException ex = new InactiveAccountException();

    // Assert
    assertNotNull(ex.getMessage());
    assertFalse(ex.getMessage().isEmpty());
  }

  @Test
  void invalidActivationTokenExceptionRetornaMensaje() {
    // Arrange - Act
    InvalidActivationTokenException ex = new InvalidActivationTokenException(
      "token invalido"
    );

    // Assert
    assertEquals("token invalido", ex.getMessage());
  }

  @Test
  void userNotFoundExceptionConMensajeRetornaMensaje() {
    // Arrange - Act
    UserNotFoundException ex = new UserNotFoundException("no existe");

    // Assert
    assertEquals("no existe", ex.getMessage());
  }

  @Test
  void userNotFoundExceptionDefaultTieneMensaje() {
    // Arrange - Act
    UserNotFoundException ex = new UserNotFoundException();

    // Assert
    assertNotNull(ex.getMessage());
    assertFalse(ex.getMessage().isEmpty());
  }

  @Test
  void emailAlreadyExitsExceptionContieneEmail() {
    // Arrange - Act
    EmailAlreadyExitsException ex = new EmailAlreadyExitsException(
      "ana@mail.com"
    );

    // Assert
    assertTrue(ex.getMessage().contains("ana@mail.com"));
  }
}
