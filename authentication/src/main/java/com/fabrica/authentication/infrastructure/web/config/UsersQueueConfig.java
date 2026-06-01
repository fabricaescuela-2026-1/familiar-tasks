package com.fabrica.authentication.infrastructure.web.config;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsersQueueConfig {

  @Value("${azure.storage.connection-string}")
  private String connectionString;

  @Value("${azure.storage.memebership-service.queue-name}")
  private String membershipServiceQueueName;

  @Value("${azure.storage.tasks-service.queue-name}")
  private String tasksServiceQueueName;

  @Bean(name = "queueHomeMember")
  public QueueClient queueClient() {
    return new QueueClientBuilder()
      .connectionString(connectionString)
      .queueName(membershipServiceQueueName)
      .buildClient();
  }

  @Bean(name = "queueTasks")
  public QueueClient queueUsersTaskClient() {
    return new QueueClientBuilder()
      .connectionString(connectionString)
      .queueName(tasksServiceQueueName)
      .buildClient();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
      .findAndRegisterModules()
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
