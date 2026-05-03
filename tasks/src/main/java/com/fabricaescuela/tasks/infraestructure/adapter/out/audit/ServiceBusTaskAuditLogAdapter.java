package com.fabricaescuela.tasks.infraestructure.adapter.out.audit;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fabricaescuela.tasks.application.dto.TaskCreatedLog;
import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceBusTaskAuditLogAdapter implements TaskAuditLogPort {

    private static final Logger logger = LoggerFactory.getLogger(ServiceBusTaskAuditLogAdapter.class);

    private final ServiceBusSenderClient senderClient;
    private final ObjectMapper objectMapper;

    public ServiceBusTaskAuditLogAdapter(ServiceBusSenderClient senderClient) {
        this.senderClient = senderClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void publishTaskCreated(UUID userId, UUID taskId) {
        TaskCreatedLog log = TaskCreatedLog.taskCreated(userId, taskId);
        try {
            String payload = objectMapper.writeValueAsString(log);
            senderClient.sendMessage(new ServiceBusMessage(payload));
            logger.info("Audit log publicado a Service Bus: {}", log);
        } catch (JsonProcessingException e) {
            logger.error("No se pudo serializar el audit log {}", log, e);
        } catch (RuntimeException e) {
            logger.error("Error publicando audit log a Service Bus: {}", log, e);
        }
    }
}
