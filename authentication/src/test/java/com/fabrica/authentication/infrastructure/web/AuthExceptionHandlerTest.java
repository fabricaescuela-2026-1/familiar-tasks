package com.fabrica.authentication.infrastructure.web;

import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InactiveAccountException;
import com.fabrica.authentication.domain.exceptions.InvalidActivationTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthExceptionHandlerTest {

    private final AuthExceptionHandler handler = new AuthExceptionHandler();

    private void assertBody(ResponseEntity<Map<String, Object>> response, HttpStatus expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedStatus.value(), response.getBody().get("status"));
        assertEquals(expectedStatus.getReasonPhrase(), response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
        assertNotNull(response.getBody().get("message"));
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void handleIllegalArgumentRetornaBadRequest() {
        assertBody(handler.handleIllegalArgument(new IllegalArgumentException("dato invalido")),
            HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleEmailExistsRetornaConflict() {
        assertBody(handler.handleEmailExists(new EmailAlreadyExitsException("a@mail.com")),
            HttpStatus.CONFLICT);
    }

    @Test
    void handleUserNotFoundRetornaUnauthorizedConMensajeGenerico() {
        ResponseEntity<Map<String, Object>> response = handler.handleUserNotFound(new UserNotFoundException());
        assertBody(response, HttpStatus.UNAUTHORIZED);
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void handleInvalidRefreshRetornaUnauthorized() {
        assertBody(handler.handleInvalidRefresh(new InvalidRefreshTokenException()), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void handleInvalidTokenRetornaUnauthorized() {
        assertBody(handler.handleInvalidToken(new InvalidTokenException()), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void handleInactiveAccountRetornaUnauthorized() {
        assertBody(handler.handleInactiveAccount(new InactiveAccountException()), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void handleInvalidActivationTokenRetornaUnauthorized() {
        assertBody(handler.handleInvalidActivationToken(
            new InvalidActivationTokenException("expirado")), HttpStatus.UNAUTHORIZED);
    }
}
