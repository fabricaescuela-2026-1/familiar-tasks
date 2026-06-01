package com.fabrica.authentication.domain.ports.out;

import com.fabrica.authentication.domain.model.TwoFactorAuthToken;
import java.util.Optional;
import java.util.UUID;

public interface TwoFactorAuthTokenRepositoryPort {
  void invalidateAllByUserEmail(String email);
  void save(TwoFactorAuthToken token);
  Optional<TwoFactorAuthToken> findLastByUserEmail(String email);
  void increaseAttemptsByOne(UUID id);
}
