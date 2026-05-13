package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.exception.InvalidDataException;
import com.udea.usermembershipservice.infrastructure.adapter.in.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void datosInvalidosRetorna400() {
        // Arrange
        InvalidDataException ex = new InvalidDataException("campo requerido");

        // Act
        ResponseEntity<?> response = handler.handleBadRequest(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void searchExceptionConCausaNotFoundRetorna404() {
        // Arrange
        SearchException ex = new SearchException("busqueda fallida",
                new RuntimeException("user not found"));

        // Act
        ResponseEntity<?> response = handler.handleSearch(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void searchExceptionSinCausaNotFoundRetorna400() {
        // Arrange
        SearchException ex = new SearchException("busqueda fallida",
                new RuntimeException("parametro incorrecto"));

        // Act
        ResponseEntity<?> response = handler.handleSearch(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void searchExceptionSinCausaRetorna400() {
        // Arrange
        SearchException ex = new SearchException("no encontrado");

        // Act
        ResponseEntity<?> response = handler.handleSearch(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void persistenceExceptionConAlreadyExistsRetorna409() {
        // Arrange
        PersistenceException ex = new PersistenceException("fallo al guardar",
                new RuntimeException("already exists"));

        // Act
        ResponseEntity<?> response = handler.handlePersistence(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void persistenceExceptionConInvalidRetorna400() {
        // Arrange
        PersistenceException ex = new PersistenceException("fallo al guardar",
                new RuntimeException("invalid data format"));

        // Act
        ResponseEntity<?> response = handler.handlePersistence(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void persistenceExceptionGenericaRetorna500() {
        // Arrange
        PersistenceException ex = new PersistenceException("error de base de datos");

        // Act
        ResponseEntity<?> response = handler.handlePersistence(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void completionExceptionConCausaNonRuntimeRetorna500() {
        // Arrange
        CompletionException ex = new CompletionException(new Exception("error generico"));

        // Act
        ResponseEntity<?> response = handler.handleCompletion(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void exceptionGenericaRetorna500() {
        // Arrange
        Exception ex = new Exception("error inesperado");

        // Act
        ResponseEntity<?> response = handler.handleGeneric(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void completionExceptionConCausaRuntimeRelanza() {
        // Arrange
        RuntimeException causa = new InvalidDataException("causa runtime");
        CompletionException ex = new CompletionException(causa);

        // Act - Assert
        assertThrows(RuntimeException.class, () -> handler.handleCompletion(ex));
    }

    @Test
    void searchExceptionMensajeDesdeCausa() {
        // Arrange
        SearchException ex = new SearchException("wrapper",
                new RuntimeException("mensaje de causa"));

        // Act
        ResponseEntity<?> response = handler.handleSearch(ex);

        // Assert
        assertNotNull(response.getBody());
    }

    @Test
    void persistenceExceptionMensajeDesdeExcepcion() {
        // Arrange
        PersistenceException ex = new PersistenceException("mensaje directo");

        // Act
        ResponseEntity<?> response = handler.handlePersistence(ex);

        // Assert
        assertNotNull(response.getBody());
    }
}
