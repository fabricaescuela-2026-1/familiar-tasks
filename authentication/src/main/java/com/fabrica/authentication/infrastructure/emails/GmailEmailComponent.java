package com.fabrica.authentication.infrastructure.emails;

import com.fabrica.authentication.application.dto.mail.EmailProperties;
import com.fabrica.authentication.application.ports.out.EmailSendingPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class GmailEmailComponent implements EmailSendingPort {

  @Autowired
  private JavaMailSender mailSender;

  @Override
  public void sendCodeEmail(EmailProperties emailProps) {
    var msg = new SimpleMailMessage();
    msg.setFrom("b.jaraba@udea.edu.co");

    msg.setTo(emailProps.recipient());
    msg.setSubject(emailProps.subject());
    msg.setText(getText(emailProps.subject(), emailProps.code()));

    mailSender.send(msg);
  }

  private String getText(String subject, String code) {
    return String.format("%s: %s", subject, code);
  }
}
