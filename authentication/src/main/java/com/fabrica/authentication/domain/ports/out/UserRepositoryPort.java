package com.fabrica.authentication.domain.ports.out;

import com.fabrica.authentication.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
  Optional<User> findByEmail(String email);

  User save(User user);

  void activateUserByEmail(String email);
}
