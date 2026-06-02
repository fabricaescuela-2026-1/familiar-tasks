package com.fabricaescuela.tasks.infraestructure.adapter.out;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fabricaescuela.tasks.domain.ports.out.UserLookupPort;
import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;
import com.fabricaescuela.tasks.infraestructure.database.jpa.UserRepository;

@Component
public class UserLookupAdapter implements UserLookupPort {

  private final UserRepository userRepository;

  public UserLookupAdapter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<UUID> findUserIdByUsername(String username) {
    if (username == null || username.isBlank()) {
      return Optional.empty();
    }
    Optional<UserEntity> found;
    try {
      found = userRepository.findUserEntityByUsername(username);
    } catch (RuntimeException ex) {
      found = Optional.empty();
    }
    if (found.isEmpty()) {
      found = userRepository.findByEmail(username);
    }
    return found.map(UserEntity::getUserId);
  }
}
