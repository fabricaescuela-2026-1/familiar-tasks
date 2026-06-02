package com.fabrica.authentication.infrastructure.database.jpa;

import com.fabrica.authentication.infrastructure.database.entities.TwoFactorAuthTokenEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TwoFactorAuthTokenJpaRepo
  extends JpaRepository<TwoFactorAuthTokenEntity, UUID>
{
  @Query(
    "SELECT t FROM TwoFactorAuthTokenEntity t WHERE t.user.email = :email ORDER BY t.createdAt DESC LIMIT 1"
  )
  Optional<TwoFactorAuthTokenEntity> findLastByUserEmail(
    @Param("email") String email
  );

  @Transactional
  @Modifying
  @Query(
    "UPDATE TwoFactorAuthTokenEntity t SET t.invalidated = TRUE WHERE t.user.email = :email"
  )
  int invalidateAllByUserEmail(@Param("email") String email);

  @Transactional
  @Modifying
  @Query(
    "UPDATE TwoFactorAuthTokenEntity t SET t.attempts = t.attempts + 1 WHERE t.id = :id"
  )
  int increaseAttemptsByOne(@Param("id") UUID id);
}
