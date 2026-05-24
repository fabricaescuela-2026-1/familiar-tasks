package com.fabricaescuela.tasks.infraestructure;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fabricaescuela.tasks.infraestructure.adapter.out.audit.ServiceBusTaskAuditLogAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBusTaskAuditLogAdapterTest {

    @Mock private ServiceBusSenderClient senderClient;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void publishTaskCreatedEnviaMensajeSerializadoAlServiceBus() {
        // Arrange
        ServiceBusTaskAuditLogAdapter adapter = new ServiceBusTaskAuditLogAdapter(senderClient);
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        ArgumentCaptor<ServiceBusMessage> captor = ArgumentCaptor.forClass(ServiceBusMessage.class);

        // Act
        adapter.publishTaskCreated(userId, taskId);

        // Assert
        verify(senderClient).sendMessage(captor.capture());
        String payload = captor.getValue().getBody().toString();
        assertTrue(payload.contains(userId.toString()));
        assertTrue(payload.contains(taskId.toString()));
        assertTrue(payload.contains("task_created"));
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void publishTaskCreatedConFalloDeServiceBusNoPropagaExcepcion() {
        // Arrange
        ServiceBusTaskAuditLogAdapter adapter = new ServiceBusTaskAuditLogAdapter(senderClient);
        doThrow(new RuntimeException("Service Bus caido"))
            .when(senderClient).sendMessage(any(ServiceBusMessage.class));

        // Act - Assert
        assertDoesNotThrow(() -> adapter.publishTaskCreated(UUID.randomUUID(), UUID.randomUUID()));
    }
}
