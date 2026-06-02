package com.fabrica.authentication.application.ports.out;

import com.fabrica.authentication.application.dto.mail.EmailProperties;

public interface EmailSendingPort {
  void sendCodeEmail(EmailProperties emailProps);
}
