package com.fabrica.authentication.infrastructure.web;

import com.azure.storage.queue.QueueClient;
import com.fabrica.authentication.application.dto.UserMessage;
import com.fabrica.authentication.application.ports.out.UserQueuePort;
import com.fabrica.authentication.domain.exceptions.UserMessageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserQueueService implements UserQueuePort {
  private final QueueClient userQueueClient;
  private final ObjectMapper objectMapper;

  @Override
  public void sendUserMessage(UserMessage user) {
    String userMessage;
    try {
      userMessage = objectMapper.writeValueAsString(user);
    } catch (JsonProcessingException e) {
      throw new UserMessageException(e.getMessage());
    }
    userQueueClient.sendMessage(userMessage);
    log.info("Mensaje enviado: {}", userMessage);
  }

}
