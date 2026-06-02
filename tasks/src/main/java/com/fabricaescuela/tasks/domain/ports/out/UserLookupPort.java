package com.fabricaescuela.tasks.domain.ports.out;

import java.util.Optional;
import java.util.UUID;

public interface UserLookupPort {
  Optional<UUID> findUserIdByUsername(String username);
}
