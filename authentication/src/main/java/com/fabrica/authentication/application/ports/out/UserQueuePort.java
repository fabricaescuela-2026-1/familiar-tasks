package com.fabrica.authentication.application.ports.out;

import com.fabrica.authentication.domain.model.User;

public interface UserQueuePort {
  void sendUserMessage(User user);

}
