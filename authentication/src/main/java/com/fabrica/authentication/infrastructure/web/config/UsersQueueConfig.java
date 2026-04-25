package com.fabrica.authentication.infrastructure.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class UsersQueueConfig {
  @Value("${azure.storage.users.connection-string}")
  private String connectionString;

  @Value("${azure.storage.users.queue-name}")
  private String queueName;

  @Bean
  public QueueClient queueClient() {
    return new QueueClientBuilder()
        .connectionString(connectionString)
        .queueName(queueName)
        .buildClient();
  }

  @Bean
  private ObjectMapper objectMapper() {
    return new ObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
