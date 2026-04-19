package com.fabrica.authentication.infrastructure.database.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabrica.authentication.infrastructure.database.entities.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);
}
