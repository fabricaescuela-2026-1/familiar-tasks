package com.fabrica.authentication.application;

import com.fabrica.authentication.application.dto.ActivationAccountResponse;
import com.fabrica.authentication.application.dto.mail.EmailProperties;
import com.fabrica.authentication.application.ports.in.AccountValidationUseCase;
import com.fabrica.authentication.application.ports.out.EmailSendingPort;
import com.fabrica.authentication.application.util.CodeGeneration;
import com.fabrica.authentication.domain.exceptions.InvalidActivationTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.ActivationToken;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.ActivationTokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import jakarta.validation.constraints.Email;
import java.security.SecureRandom;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountValidationService implements AccountValidationUseCase {

  private final ActivationTokenRepositoryPort activationTokenRepo;
  private final PasswordEncoder passwordEncoder;
  private final UserRepositoryPort userRepo;
  private final EmailSendingPort emailSendingComp;

  @Override
  @Transactional
  public void createActivationToken(String email) {
    var user = getUser(email);
    var code = CodeGeneration.generateSixDigitCode();
    var codeHash = passwordEncoder.encode(code);

    var token = ActivationToken.builder()
      .email(email)
      .id(UUID.randomUUID())
      .codeHash(codeHash)
      .build();

    activationTokenRepo.invalidateAllByUserEmail(email);
    activationTokenRepo.save(token, user);
    var emailContent = getEmailContent(email, code);
    emailSendingComp.sendCodeEmail(emailContent);
  }

  private EmailProperties getEmailContent(String email, String code) {
    return EmailProperties.builder()
      .code(code)
      .recipient(email)
      .subject("Código de activación")
      .build();
  }

  private User getUser(String email) {
    return userRepo.findByEmail(email).orElseThrow(UserNotFoundException::new);
  }

  @Override
  @Transactional
  public ActivationAccountResponse activateAccount(String email, String code) {
    var user = getUser(email);
    var token = getToken(email);

    activationTokenRepo.increaseAttemptsByOne(token.getId());

    token.validateToken();
    if (!passwordEncoder.matches(code, token.getCodeHash())) {
      throw new InvalidActivationTokenException("Código incorrecto");
    }

    userRepo.activateUserByEmail(email);
    activationTokenRepo.invalidateAllByUserEmail(email);

    return getResponse(email, user);
  }

  private ActivationAccountResponse getResponse(String email, User user) {
    return ActivationAccountResponse.builder()
      .email(email)
      .userId(user.getUserId())
      .activated(true)
      .build();
  }

  private ActivationToken getToken(String email) {
    return activationTokenRepo
      .findLastByUserEmail(email)
      .orElseThrow(() ->
        new InvalidActivationTokenException(
          "No hay token de activación para el usuario"
        )
      );
  }
}
