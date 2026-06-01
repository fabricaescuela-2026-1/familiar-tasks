package com.fabrica.authentication.infrastructure.database.jpa;

import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);

  @Modifying
  @Transactional
  @Query("UPDATE UserEntity u SET u.active = TRUE WHERE u.email = :email")
  int activateUserByEmail(@Param("email") String email);
}
