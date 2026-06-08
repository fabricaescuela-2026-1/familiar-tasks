package com.fabrica.authentication.infrastructure.emails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fabrica.authentication.application.dto.mail.EmailProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class GmailEmailComponentTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private GmailEmailComponent component;

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void sendCodeEmailEnviaMensajeConPropiedadesCorrectas() {
    // Arrange
    EmailProperties props = EmailProperties.builder()
      .code("123456")
      .recipient("ana@mail.com")
      .subject("Codigo de verificacion")
      .build();

    // Act
    component.sendCodeEmail(props);

    // Assert
    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(
      SimpleMailMessage.class
    );
    verify(mailSender).send(captor.capture());
    SimpleMailMessage sent = captor.getValue();
    assertArrayEquals(new String[] { "ana@mail.com" }, sent.getTo());
    assertEquals("Codigo de verificacion", sent.getSubject());
    assertEquals("Codigo de verificacion: 123456", sent.getText());
    assertEquals("b.jaraba@udea.edu.co", sent.getFrom());
  }
}
