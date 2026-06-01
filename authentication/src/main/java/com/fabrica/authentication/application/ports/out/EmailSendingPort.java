package com.fabrica.authentication.application.ports.out;

import com.fabrica.authentication.application.dto.mail.EmailAccountVerification;

public interface EmailSendingPort {
  void sendVerificationEmail(EmailAccountVerification emailAccoundVerification);
}
