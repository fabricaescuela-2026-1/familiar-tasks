package com.fabricaescuela.logs.infrastructure.adapter.in;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.fabricaescuela.logs.application.dto.LogRequest;
import com.fabricaescuela.logs.domain.ports.in.CreateLogUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class LogMessageListener {

    private final CreateLogUseCase createLogUseCase;
    private final ObjectMapper objectMapper;

    @Value("${spring.jms.servicebus.connection-string}")
    private String connectionString;

    private ServiceBusProcessorClient processorClient;

    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        processorClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .processor()
                .queueName("audit_log_queue_name")
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .prefetchCount(10)
                .maxConcurrentCalls(1)
                .processMessage(context -> {
                    try {
                        BinaryData body = context.getMessage().getBody();
                        String messageJson = body.toString();
                        log.info("📨 Received message from Azure Service Bus: {}", messageJson);

                        // Deserialize JSON to LogRequest
                        LogRequest logRequest = objectMapper.readValue(messageJson, LogRequest.class);

                        // Save log using the existing service
                        createLogUseCase.execute(
                                logRequest.id(),
                                logRequest.idUser(),
                                logRequest.modifiedElement(),
                                logRequest.action()
                        );

                        log.info("✅ Log saved successfully from message queue");
                    } catch (Exception e) {
                        log.error("❌ Error processing log message from queue: {}", e.getMessage(), e);
                    }
                })
                .processError(context -> log.error("Error: {}", context.getException().getMessage(), context.getException()))
                .buildProcessorClient();

        processorClient.start();
        log.info("🚀 Azure Service Bus listener started");
    }

    public void stopListening() {
        if (processorClient != null) {
            processorClient.close();
            log.info("🛑 Azure Service Bus listener stopped");
        }
    }
}
