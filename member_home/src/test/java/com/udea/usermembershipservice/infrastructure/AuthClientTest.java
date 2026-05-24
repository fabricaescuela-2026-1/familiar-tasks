package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.aplication.useCase.dto.auth.AuthRefreshResponse;
import com.udea.usermembershipservice.infrastructure.adapter.out.auth.AuthClient;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

    @Mock private RestTemplate restTemplate;

    @InjectMocks
    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authClient, "authServiceUrl", "http://auth:8080");
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void refreshAccessTokenRetornaRespuestaEnExitoso() {
        // Arrange
        AuthRefreshResponse body = AuthRefreshResponse.builder()
            .accessToken("nuevo-access")
            .refreshToken("nuevo-refresh")
            .build();
        ResponseEntity<AuthRefreshResponse> respuestaOk = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(AuthRefreshResponse.class)))
            .thenReturn(respuestaOk);

        // Act
        AuthRefreshResponse resultado = authClient.refreshAccessToken("token-original");

        // Assert
        assertEquals("nuevo-access", resultado.getAccessToken());
        assertEquals("nuevo-refresh", resultado.getRefreshToken());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void refreshAccessTokenConBodyNuloLanzaIllegalState() {
        // Arrange
        ResponseEntity<AuthRefreshResponse> respuestaVacia = ResponseEntity.ok().body(null);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(AuthRefreshResponse.class)))
            .thenReturn(respuestaVacia);

        // Act - Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> authClient.refreshAccessToken("token"));
        assertTrue(ex.getMessage().contains("respuesta vacía"));
    }

    @Test
    void refreshAccessTokenConErrorDeConexionLanzaIllegalState() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(AuthRefreshResponse.class)))
            .thenThrow(new RestClientException("connection refused"));

        // Act - Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> authClient.refreshAccessToken("token"));
        assertTrue(ex.getMessage().contains("servicio de autenticación no disponible"));
    }
}
