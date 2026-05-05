package com.fabrica.authentication.infrastructure.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.azure.storage.queue.QueueClient;
import com.fabrica.authentication.application.dto.UserMessage;
import com.fabrica.authentication.application.ports.out.UserQueuePort;
import com.fabrica.authentication.domain.exceptions.UserMessageException;
import com.fabrica.authentication.domain.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserQueueService implements UserQueuePort {
  private final QueueClient homeMemberQueue;
  private final ObjectMapper objectMapper;
  private final QueueClient tasksQueue;

  public UserQueueService(
      @Qualifier("queueHomeMember") QueueClient homeMemberQueue,
      @Qualifier("queueTasks") QueueClient tasksQueue,
      ObjectMapper objectMapper) {
    this.homeMemberQueue = homeMemberQueue;
    this.tasksQueue = tasksQueue;
    this.objectMapper = objectMapper;
  }

  @Override
  public void sendUserMessage(User user) {
    UserMessage userMessage = UserMessage.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .lastname(user.getLastname())
        .email(user.getEmail())
        .passwordHash(user.getPasswordHash())
        .createdAt(user.getCreatedAt())
        .build();
    try {
      String message = objectMapper.writeValueAsString(userMessage);
      homeMemberQueue.sendMessage(message);
      tasksQueue.sendMessage(message);
      log.info("Mensaje enviado: {}", userMessage);
    } catch (JsonProcessingException e) {
      throw new UserMessageException(e.getMessage());
    }
  }

}
