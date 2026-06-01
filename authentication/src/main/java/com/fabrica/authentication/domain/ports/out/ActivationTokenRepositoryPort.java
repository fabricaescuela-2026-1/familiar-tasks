package com.fabrica.authentication.domain.ports.out;

import com.fabrica.authentication.domain.model.ActivationToken;
import java.util.Optional;

public interface ActivationTokenRepositoryPort {
  void save(ActivationToken token);
  void invalidateAllByUserEmail(String email);
  Optional<ActivationToken> findLastByUserEmail(String email);
}
