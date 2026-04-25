package com.fabrica.authentication.application.ports.out;

import com.fabrica.authentication.application.dto.UserMessage;

public interface UserQueuePort {
  void sendUserMessage(UserMessage user);

}
