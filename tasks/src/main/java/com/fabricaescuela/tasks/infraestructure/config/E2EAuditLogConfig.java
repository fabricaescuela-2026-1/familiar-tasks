package com.fabricaescuela.tasks.infraestructure.config;

import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Solo se activa cuando SPRING_PROFILES_ACTIVE=e2e.
 * Provee un TaskAuditLogPort no-op porque el SDK azure-messaging-servicebus 7.17.5
 * no respeta el flag UseDevelopmentEmulator=true del Azure Service Bus Emulator
 * (intenta puerto 5671 TLS en lugar del 5672 plano que expone el emulator).
 * Sin este bean, cada creacion de tarea queda atrapada minutos en retry del SDK.
 * Cero impacto en produccion (perfil e2e no se activa por defecto).
 */
@Configuration
@Profile("e2e")
public class E2EAuditLogConfig {

    @Bean
    @Primary
    public TaskAuditLogPort taskAuditLogPortNoOp() {
        return new TaskAuditLogPort() {
            @Override public void publishTaskCreated(java.util.UUID userId, java.util.UUID taskId) { /* no-op */ }
            @Override public void publishTaskStatusChanged(java.util.UUID userId, java.util.UUID taskId, String newStatus) { /* no-op */ }
        };
    }
}
