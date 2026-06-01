package com.fabrica.authentication.application.ports.in;

import com.fabrica.authentication.application.dto.ActivationAccountResponse;

public interface AccountValidationUseCase {
  void createActivationToken(String email);

  ActivationAccountResponse activateAccount(String email, String code);
}
