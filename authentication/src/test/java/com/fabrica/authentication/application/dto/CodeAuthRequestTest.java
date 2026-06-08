package com.fabrica.authentication.application.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CodeAuthRequestTest {

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void instanciaPreservaCamposEmailYCodigo() {
    // Arrange - Act
    CodeAuthRequest request = new CodeAuthRequest("ana@mail.com", "123456");

    // Assert
    assertEquals("ana@mail.com", request.email());
    assertEquals("123456", request.code());
  }
}
