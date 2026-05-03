package com.udea.usermembershipservice.infrastructure.adapter.out.audit;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.usermembershipservice.aplication.port.out.IAuditLogQueuePort;
import com.udea.usermembershipservice.aplication.useCase.dto.audit.RoleChangedLog;

public class ServiceBusAuditLogQueueAdapter implements IAuditLogQueuePort {

    private static final Logger logger = LoggerFactory.getLogger(ServiceBusAuditLogQueueAdapter.class);

    private final ServiceBusSenderClient senderClient;
    private final ObjectMapper objectMapper;

    public ServiceBusAuditLogQueueAdapter(ServiceBusSenderClient senderClient, ObjectMapper objectMapper) {
        this.senderClient = senderClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishRoleChanged(UUID userId, String modifiedElement) {
        RoleChangedLog log = RoleChangedLog.roleChanged(userId, modifiedElement);
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
