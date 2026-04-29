package com.fabricaescuela.tasks.infraestructure.adapter.out;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fabricaescuela.tasks.application.dto.AuthRefreshResponse;

import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Component
@RequiredArgsConstructor
@Log
public class AuthClient {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public AuthRefreshResponse refreshAccessToken(String refreshToken) {
        try {
            String refreshUrl = authServiceUrl + "/auth/refresh";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> request = new HttpEntity<>(refreshToken, headers);
            
            ResponseEntity<AuthRefreshResponse> response = restTemplate.postForEntity(
                    refreshUrl,
                    request,
                    AuthRefreshResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Token refrescado exitosamente");
                return response.getBody();
            } else {
                throw new RuntimeException("Falló al refrescar el token: respuesta vacía");
            }
        } catch (RestClientException e) {
            log.severe("Error al conectar con el servicio de autenticación: " + e.getMessage());
            throw new RuntimeException("No se pudo refrescar el token: servicio de autenticación no disponible", e);
        } catch (Exception e) {
            log.severe("Error inesperado al refrescar el token: " + e.getMessage());
            throw new RuntimeException("Error al refrescar el token", e);
        }
    }
}
