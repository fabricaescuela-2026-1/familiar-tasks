package com.udea.usermembershipservice.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

/**
 * Smoke test contra el Service Bus real. Falla si las credenciales o la cola estan mal.
 * Solo corre cuando AUDIT_LOG_CONNECTION_STRING y AUDIT_LOG_QUEUE_NAME estan seteadas.
 *
 * Para ejecutar localmente (PowerShell):
 *   $env:AUDIT_LOG_CONNECTION_STRING="Endpoint=sb://registrerlogs.servicebus.windows.net/;SharedAccessKeyName=...;SharedAccessKey=..."
 *   $env:AUDIT_LOG_QUEUE_NAME="audit_log_queue_name"
 *   ./mvnw test -Dtest=AuditLogServiceBusSmokeIT
 */
class AuditLogServiceBusSmokeIT {

    @Test
    @EnabledIfEnvironmentVariable(named = "AUDIT_LOG_CONNECTION_STRING", matches = ".+")
    @EnabledIfEnvironmentVariable(named = "AUDIT_LOG_QUEUE_NAME", matches = ".+")
    void enviaMensajeRealAlServiceBus() {
        String connectionString = System.getenv("AUDIT_LOG_CONNECTION_STRING");
        String queueName = System.getenv("AUDIT_LOG_QUEUE_NAME");

        try (ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient()) {

            String payload = "{\"logId\":\"00000000-0000-0000-0000-000000000099\","
                    + "\"userId\":\"00000000-0000-0000-0000-000000000001\","
                    + "\"modifiedElement\":\"smoke-test@familiar-tasks.local\","
                    + "\"action\":\"role_changed\"}";
            sender.sendMessage(new ServiceBusMessage(payload));
        }
    }
}
