package com.fabrica.authentication.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fabrica.authentication.application.dto.ActivationAccountResponse;
import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.CodeAuthRequest;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.application.ports.in.AccountValidationUseCase;
import com.fabrica.authentication.application.ports.in.AuthUseCase;
import com.fabrica.authentication.infrastructure.web.AuthController;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private AuthUseCase authUseCase;

  @Mock
  private AccountValidationUseCase accountValidationUseCase;

  @InjectMocks
  private AuthController controller;

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void loginRestorna200EnviandoEmail() {
    // Arrange
    LoginRequest request = new LoginRequest("carlos@mail.com", "pass1234");

    // Act
    ResponseEntity<Map<String, String>> resultado = controller.login(request);

    // Assert
    assertEquals(HttpStatus.OK, resultado.getStatusCode());
    assertEquals(
      "Revisa tu correo e ingresa el código de verificación",
      resultado.getBody().get("message")
    );
    verify(authUseCase).login(request);
  }

  @Test
  void registerRetorna201ConTokens() {
    // Arrange
    RegisterRequest request = new RegisterRequest(
      "Carlos",
      "Ruiz",
      "carlos@mail.com",
      "Segura@1"
    );

    // Act
    ResponseEntity<Map<String, String>> resultado = controller.register(
      request
    );

    // Assert
    assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
    assertEquals(
      "Usuario registrado exitosamente",
      resultado.getBody().get("message")
    );
    verify(authUseCase).register(request);
  }

  @Test
  void refreshRetorna200ConNuevoAccessToken() {
    // Arrange
    AuthResponse response = new AuthResponse("nuevo-access", "refresh");
    when(authUseCase.refreshToken("refresh-hash")).thenReturn(response);

    // Act
    ResponseEntity<AuthResponse> resultado = controller.refresh("refresh-hash");

    // Assert
    assertEquals(HttpStatus.OK, resultado.getStatusCode());
    assertEquals("nuevo-access", resultado.getBody().accessToken());
  }

  @Test
  void getTokenRetorna200ConDetalles() {
    // Arrange
    UUID tokenId = UUID.randomUUID();
    TokenResponse response = TokenResponse.builder()
      .tokenId(tokenId)
      .tokenHash("hash")
      .tokenType("ACCESS")
      .build();
    when(authUseCase.getToken("hash")).thenReturn(response);

    // Act
    ResponseEntity<TokenResponse> resultado = controller.getToken("hash");

    // Assert
    assertEquals(HttpStatus.OK, resultado.getStatusCode());
    assertEquals(tokenId, resultado.getBody().tokenId());
  }

  @Test
  void getActivationCodeRetorna200() {
    // Arrange - Act
    ResponseEntity<Map<String, String>> resultado =
      controller.getActivationCode("ana@mail.com");

    // Assert
    assertEquals(HttpStatus.OK, resultado.getStatusCode());
    assertEquals(
      "Código de activación enviado",
      resultado.getBody().get("message")
    );
    verify(accountValidationUseCase).createActivationToken("ana@mail.com");
  }

  @Test
  void verifyTwoFactorAuthCodeRetorna200ConAuthResponse() {
    // Arrange
    CodeAuthRequest request = new CodeAuthRequest("ana@mail.com", "123456");
    AuthResponse response = new AuthResponse("access", "refresh");
    when(
      authUseCase.verifyTwoFactorAuthCode("123456", "ana@mail.com")
    ).thenReturn(response);

    // Act
    ResponseEntity<AuthResponse> resultado =
      controller.verifyTwoFactorAuthCode(request);

    // Assert
    assertEquals(HttpStatus.OK, resultado.getStatusCode());
    assertEquals("access", resultado.getBody().accessToken());
    assertEquals("refresh", resultado.getBody().refreshToken());
  }

  @Test
  void activateAccountRetorna200ConActivationResponse() {
    // Arrange
    CodeAuthRequest request = new CodeAuthRequest("ana@mail.com", "abc123");
    UUID userId = UUID.randomUUID();
    ActivationAccountResponse response = ActivationAccountResponse.builder()
      .email("ana@mail.com")
      .userId(userId)
      .activated(true)
      .build();
    when(
      accountValidationUseCase.activateAccount("ana@mail.com", "abc123")
    ).thenReturn(response);

    // Act
    ResponseEntity<ActivationAccountResponse> resultado =
      controller.activateAccount(request);

    // Assert
    assertEquals(HttpStatus.OK, resultado.getStatusCode());
    assertEquals("ana@mail.com", resultado.getBody().email());
    assertEquals(userId, resultado.getBody().userId());
    assertTrue(resultado.getBody().activated());
  }
}
