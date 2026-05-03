package com.fabricaescuela.tasks.infraestructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import com.fabricaescuela.tasks.infraestructure.adapter.out.audit.ServiceBusTaskAuditLogAdapter;

@Configuration
public class AuditLogConfig {

    @Bean(destroyMethod = "close")
    public ServiceBusSenderClient auditLogSenderClient(
            @Value("${audit.log.servicebus.connection-string}") String connectionString,
            @Value("${audit.log.servicebus.queue-name}") String queueName
    ) {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient();
    }

    @Bean
    public TaskAuditLogPort taskAuditLogPort(ServiceBusSenderClient auditLogSenderClient) {
        return new ServiceBusTaskAuditLogAdapter(auditLogSenderClient);
    }
}
