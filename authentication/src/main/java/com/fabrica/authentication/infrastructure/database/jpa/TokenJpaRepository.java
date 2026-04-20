package com.fabrica.authentication.infrastructure.database.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fabrica.authentication.infrastructure.database.entities.TokenEntity;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, UUID> {
  Optional<TokenEntity> findByTokenHash(String tokenHash);

  @Query("SELECT t FROM TokenEntity t WHERE t.user.id = :userId")
  List<TokenEntity> findAllByUserId(UUID userId);

  @Query("UPDATE TokenEntity t SET t.expiratedAt = CURRENT_TIMESTAMP WHERE t.user.email = :email")
  void revokeAllByUserEmail(String email);
}
