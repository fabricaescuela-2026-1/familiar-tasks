package com.udea.usermembershipservice.infrastructure;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.udea.usermembershipservice.infrastructure.adapter.out.audit.ServiceBusAuditLogQueueAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBusAuditLogQueueAdapterTest {

    @Mock private ServiceBusSenderClient senderClient;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void publishRoleChangedEnviaMensajeSerializadoAlServiceBus() {
        // Arrange
        ServiceBusAuditLogQueueAdapter adapter = new ServiceBusAuditLogQueueAdapter(senderClient);
        UUID userId = UUID.randomUUID();
        ArgumentCaptor<ServiceBusMessage> captor = ArgumentCaptor.forClass(ServiceBusMessage.class);

        // Act
        adapter.publishRoleChanged(userId, "ROLE_ADMIN");

        // Assert
        verify(senderClient).sendMessage(captor.capture());
        String payload = captor.getValue().getBody().toString();
        assertTrue(payload.contains(userId.toString()));
        assertTrue(payload.contains("ROLE_ADMIN"));
        assertTrue(payload.contains("role_changed"));
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void publishRoleChangedConFalloDeServiceBusNoPropagaExcepcion() {
        // Arrange
        ServiceBusAuditLogQueueAdapter adapter = new ServiceBusAuditLogQueueAdapter(senderClient);
        doThrow(new RuntimeException("Service Bus caido"))
            .when(senderClient).sendMessage(any(ServiceBusMessage.class));

        // Act - Assert
        assertDoesNotThrow(() -> adapter.publishRoleChanged(UUID.randomUUID(), "ROLE_USER"));
    }
}
