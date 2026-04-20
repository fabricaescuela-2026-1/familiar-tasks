package com.fabrica.authentication.domain.ports.out;

import java.util.Optional;

import com.fabrica.authentication.domain.model.User;

public interface UserRepositoryPort {
  Optional<User> findByEmail(String email);

  User save(User user);
}