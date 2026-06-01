package com.fabrica.authentication.infrastructure.database.jpa;

import com.fabrica.authentication.infrastructure.database.entities.ActivationTokenEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ActivationTokenJpaRepository
  extends JpaRepository<ActivationTokenEntity, UUID>
{
  @Query(
    "SELECT t FROM ActivationTokenEntity t WHERE t.user.email = :email ORDER BY t.createdAt DESC LIMIT 1"
  )
  Optional<ActivationTokenEntity> findLastByUserEmail(
    @Param("email") String email
  );

  @Modifying
  @Transactional
  @Query(
    "UPDATE ActivationTokenEntity t SET t.invalidated = TRUE WHERE t.user.email = :email"
  )
  int invalidateAllByUserEmail(@Param("email") String email);
}
