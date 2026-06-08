package com.fabricaescuela.tasks.infraestructure.adapter.out;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fabricaescuela.tasks.application.dto.AuthRefreshResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private AuthClient authClient;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
      authClient,
      "authServiceUrl",
      "http://auth-test"
    );
  }

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void refreshAccessTokenExitosoRetornaRespuesta() {
    // Arrange
    AuthRefreshResponse body = AuthRefreshResponse.builder()
      .accessToken("nuevo-access")
      .refreshToken("nuevo-refresh")
      .build();
    ResponseEntity<AuthRefreshResponse> ok = new ResponseEntity<>(
      body,
      HttpStatus.OK
    );
    when(
      restTemplate.postForEntity(
        eq("http://auth-test/auth/refresh"),
        any(HttpEntity.class),
        eq(AuthRefreshResponse.class)
      )
    ).thenReturn(ok);

    // Act
    AuthRefreshResponse result = authClient.refreshAccessToken("refresh-token");

    // Assert
    assertEquals("nuevo-access", result.getAccessToken());
    assertEquals("nuevo-refresh", result.getRefreshToken());
  }

  // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

  @Test
  void refreshAccessTokenConBodyVacioLanzaIllegalState() {
    // Arrange
    ResponseEntity<AuthRefreshResponse> empty = ResponseEntity.ok((AuthRefreshResponse) null);
    when(
      restTemplate.postForEntity(
        any(String.class),
        any(HttpEntity.class),
        eq(AuthRefreshResponse.class)
      )
    ).thenReturn(empty);

    // Act - Assert
    assertThrows(IllegalStateException.class, () ->
      authClient.refreshAccessToken("refresh-token")
    );
  }

  @Test
  void refreshAccessTokenConRestClientExceptionLanzaIllegalState() {
    // Arrange
    when(
      restTemplate.postForEntity(
        any(String.class),
        any(HttpEntity.class),
        eq(AuthRefreshResponse.class)
      )
    ).thenThrow(new RestClientException("conexion fallida"));

    // Act - Assert
    IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
      authClient.refreshAccessToken("refresh-token")
    );
    assertTrue(ex.getMessage().contains("autenticación"));
  }

  @Test
  void refreshAccessTokenConExcepcionInesperadaLanzaIllegalState() {
    // Arrange
    when(
      restTemplate.postForEntity(
        any(String.class),
        any(HttpEntity.class),
        eq(AuthRefreshResponse.class)
      )
    ).thenThrow(new RuntimeException("error inesperado"));

    // Act - Assert
    assertThrows(IllegalStateException.class, () ->
      authClient.refreshAccessToken("refresh-token")
    );
  }
}
