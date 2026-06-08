package com.fabrica.authentication.infrastructure;

import com.azure.storage.queue.QueueClient;
import com.fabrica.authentication.domain.exceptions.UserMessageException;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.web.UserQueueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQueueServiceTest {

    @Mock private QueueClient homeMemberQueue;
    @Mock private QueueClient tasksQueue;
    @Mock private ObjectMapper objectMapper;

    private UserQueueService service;

    @BeforeEach
    void setUp() {
        service = new UserQueueService(homeMemberQueue, tasksQueue, objectMapper);
    }

    private User usuario() {
        return User.builder()
            .userId(UUID.randomUUID())
            .name("Ana")
            .lastname("Lopez")
            .email("a@mail.com")
            .passwordHash("hash")
            .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0, 0))
            .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void sendUserMessageEnviaJsonAAmbasColas() throws Exception {
        // Arrange
        User user = usuario();
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"userId\":\"x\"}");

        // Act
        service.sendUserMessage(user);

        // Assert
        verify(homeMemberQueue).sendMessage("{\"userId\":\"x\"}");
        verify(tasksQueue).sendMessage("{\"userId\":\"x\"}");
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void sendUserMessageConFalloDeSerializacionLanzaUserMessageException() throws Exception {
        // Arrange
        when(objectMapper.writeValueAsString(any()))
            .thenThrow(new JsonProcessingException("error serializacion") {});

        // Act - Assert
        UserMessageException ex = assertThrows(UserMessageException.class,
            () -> service.sendUserMessage(usuario()));
        assertTrue(ex.getMessage().contains("error serializacion"));
        verify(homeMemberQueue, never()).sendMessage(anyString());
        verify(tasksQueue, never()).sendMessage(anyString());
    }
}
