package com.udea.usermembershipservice.infrastructure.config;

import com.udea.usermembershipservice.aplication.port.out.IAuditLogQueuePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Solo se activa cuando SPRING_PROFILES_ACTIVE=e2e.
 * Provee un IAuditLogQueuePort no-op porque el SDK azure-messaging-servicebus 7.17.5
 * no respeta el flag UseDevelopmentEmulator=true del Azure Service Bus Emulator
 * (intenta puerto 5671 TLS en lugar del 5672 plano que expone el emulator).
 * Sin este bean, cada cambio de rol queda atrapado minutos en retry del SDK.
 * Cero impacto en produccion (perfil e2e no se activa por defecto).
 */
@Configuration
@Profile("e2e")
public class E2EAuditLogConfig {

    @Bean
    @Primary
    public IAuditLogQueuePort auditLogQueuePortNoOp() {
        return (userId, modifiedElement) -> { /* no-op */ };
    }
}
