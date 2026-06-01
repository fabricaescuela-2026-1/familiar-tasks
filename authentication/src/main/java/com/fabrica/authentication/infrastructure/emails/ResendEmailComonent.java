package com.fabrica.authentication.infrastructure.emails;

import com.fabrica.authentication.application.dto.mail.EmailAccountVerification;
import com.fabrica.authentication.application.ports.out.EmailSendingPort;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResendEmailComonent implements EmailSendingPort {

  private final Resend resend;

  public ResendEmailComonent(
    @Value("${application.resend.email.api-key}") String apiKey
  ) {
    this.resend = new Resend(apiKey);
  }

  @Override
  public void sendVerificationEmail(
    EmailAccountVerification emailAccountVerification
  ) {
    var email = new CreateEmailOptions.Builder()
      .from("Familiar Tasks  <onboarding@resend.dev>")
      .to(emailAccountVerification.recipient())
      .subject("Verificación de la cuenta")
      .html(
        "<h1>Verifica tu cuenta de <strong>Familiar Tasks</strong></h1>" +
          "<p>Tu código de verificación es: <strong>" +
          emailAccountVerification.code() +
          "</strong></p>"
      )
      .build();

    try {
      var emailResponse = resend.emails().send(email);
      log.info(
        "Email enviado a la direccion: {}, con respuesta: {}",
        emailAccountVerification.recipient(),
        emailResponse
      );
    } catch (Exception e) {
      log.error("Error al enviar el email: {}", e.getMessage());
    }
  }
}
