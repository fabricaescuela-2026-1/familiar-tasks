package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.domain.exceptions.PriorityNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.StatusNotFoundException;
import com.fabricaescuela.tasks.infraestructure.config.GlobalExceptionHandler;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ProblemDetails;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void noResourceFoundExceptionRetorna404() {
        // Arrange
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        when(ex.getMessage()).thenReturn("no encontrado");
        when(ex.getStackTrace()).thenReturn(new StackTraceElement[]{
            new StackTraceElement("Clase", "metodo", "Clase.java", 1)
        });

        // Act
        ResponseEntity<ProblemDetails> response = handler.handleNoResourceFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource Not Found", response.getBody().getTitle());
    }

    @Test
    void illegalArgumentExceptionRetorna400() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("argumento inválido");

        // Act
        ResponseEntity<ProblemDetails> response = handler.handleIllegalArgumentException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Illegal Argument", response.getBody().getTitle());
        assertEquals("argumento inválido", response.getBody().getDetail());
    }

    @Test
    void statusNotFoundExceptionRetorna400() {
        // Arrange
        StatusNotFoundException ex = new StatusNotFoundException("DONE");

        // Act
        ResponseEntity<ProblemDetails> response = handler.handleStatusNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Status Not Found", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("DONE"));
    }

    @Test
    void priorityNotFoundExceptionRetorna400() {
        // Arrange
        PriorityNotFoundException ex = new PriorityNotFoundException("URGENTE");

        // Act
        ResponseEntity<ProblemDetails> response = handler.handlePriorityNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Priority Not Found", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("URGENTE"));
    }
}
