package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.domain.exceptions.ForbiddenTaskOperationException;
import com.fabricaescuela.tasks.domain.exceptions.PriorityNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.StatusNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.TaskNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.UserNotValidException;
import com.fabricaescuela.tasks.infraestructure.config.GlobalExceptionHandler;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ProblemDetails;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    void taskNotFoundExceptionRetorna404() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskNotFoundException ex = new TaskNotFoundException(taskId);

        // Act
        ResponseEntity<ProblemDetails> response = handler.handleTaskNotFound(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Task Not Found", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains(taskId.toString()));
    }

    @Test
    void forbiddenTaskOperationExceptionRetorna403() {
        // Arrange
        ForbiddenTaskOperationException ex = new ForbiddenTaskOperationException("no permitido");

        // Act
        ResponseEntity<ProblemDetails> response = handler.handleForbiddenTaskOperation(ex);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden Task Operation", response.getBody().getTitle());
        assertEquals("no permitido", response.getBody().getDetail());
    }

    @Test
    void userNotValidExceptionRetorna400() {
        // Arrange
        UserNotValidException ex = new UserNotValidException("usuario no valido");

        // Act
        ResponseEntity<ProblemDetails> response = handler.handleUserNotValidException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User Not Valid", response.getBody().getTitle());
        assertEquals("usuario no valido", response.getBody().getDetail());
    }

    @Test
    void methodArgumentNotValidExceptionRetorna400() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(br.getFieldErrors()).thenReturn(Collections.emptyList());
        when(ex.getBindingResult()).thenReturn(br);
        MethodParameter mp = mock(MethodParameter.class);
        when(ex.getParameter()).thenReturn(mp);
        try {
            when(mp.getMethod()).thenReturn(String.class.getMethod("trim"));
        } catch (NoSuchMethodException e) {
            fail(e);
        }

        // Act
        ResponseEntity<ProblemDetails> response = handler.handleValidationExceptions(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error", response.getBody().getTitle());
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
