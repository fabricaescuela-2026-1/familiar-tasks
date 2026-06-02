package com.fabrica.authentication.infrastructure.emails;

import com.fabrica.authentication.application.dto.mail.EmailProperties;
import com.fabrica.authentication.application.ports.out.EmailSendingPort;
import com.fabrica.authentication.domain.exceptions.EmailSendingException;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResendEmailComponent implements EmailSendingPort {

  private final Resend resend;

  public ResendEmailComponent(
    @Value("${application.resend.email.api-key}") String apiKey
  ) {
    this.resend = new Resend(apiKey);
  }

  @Override
  public void sendCodeEmail(EmailProperties emailProps) {
    var email = createEmailOptions(emailProps);

    try {
      var emailResponse = resend.emails().send(email);
      log.info(
        "Email enviado a la direccion: {}, con respuesta: {}",
        emailProps.recipient(),
        emailResponse
      );
    } catch (Exception e) {
      handleEmailException(e);
    }
  }

  private void handleEmailException(Exception e) {
    log.error("Error al enviar el email: {}", e.getMessage());
    throw new EmailSendingException("Error al enviar el email");
  }

  private CreateEmailOptions createEmailOptions(EmailProperties emailProps) {
    return new CreateEmailOptions.Builder()
      .from("Familiar Tasks <onboarding@resend.dev>")
      .to(emailProps.recipient())
      .subject(emailProps.subject())
      .html(buildEmailHtml(emailProps.code(), emailProps.subject()))
      .build();
  }

  private String buildEmailHtml(String code, String title) {
    return (
      "<h1><strong>Familiar Tasks:</strong>" +
      title +
      "</h1>" +
      "<p>Tu código de verificación es: <strong>" +
      code +
      "</strong></p>"
    );
  }
}
