package com.fabricaescuela.tasks.infraestructure.adapter.out;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fabricaescuela.tasks.application.UserSynchronizationService;
import com.fabricaescuela.tasks.application.dto.UserRegistrationEvent;

@Component
public class UserRegistrationMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationMessageListener.class);

    @Value("${spring.storage.queue.connection-string}")
    private String connectionString;

    @Value("${spring.storage.queue.name}")
    private String queueName;

    private QueueClient queueClient;
    private final UserSynchronizationService userSynchronizationService;
    private final ObjectMapper objectMapper;

    public UserRegistrationMessageListener(UserSynchronizationService userSynchronizationService) {
        this.userSynchronizationService = userSynchronizationService;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing Azure Storage Queue client for queue: {}", queueName);

        queueClient = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .buildClient();

        boolean created = queueClient.createIfNotExists();
        if (!created) {
            logger.warn("Queue {} already existed or could not be created.", queueName);
        }
    }

    @Scheduled(fixedDelayString = "5000", initialDelayString = "2000")
    public void pollQueue() {
        try {
            for (QueueMessageItem messageItem : queueClient.receiveMessages(10)) {
                processMessage(messageItem);
            }
        } catch (Exception e) {
            logger.error("Error polling Azure Storage Queue {}", queueName, e);
        }
    }

    private void processMessage(QueueMessageItem messageItem) {
        String messageBody = messageItem.getMessageText();

        logger.info("Received user registration message: {}", messageBody);
        logger.info("Message ID: {}", messageItem.getMessageId());

        try {
            // Parsear el JSON a UserRegistrationEvent
            UserRegistrationEvent event = objectMapper.readValue(messageBody, UserRegistrationEvent.class);

            // Delegar la sincronización del usuario al servicio
            userSynchronizationService.synchronizeUser(event);

            // Eliminar el mensaje de la cola solo si fue procesado exitosamente
            queueClient.deleteMessage(messageItem.getMessageId(), messageItem.getPopReceipt());
            logger.info("User registration message processed and deleted successfully");

        } catch (Exception e) {
            logger.error("Error processing user registration message", e);
            // Al no eliminar el mensaje, este volverá a la cola cuando expire el tiempo de visibilidad.
        }
    }
}