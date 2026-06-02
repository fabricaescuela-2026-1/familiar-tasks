package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.model.ActivationToken;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.ActivationTokenRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.mappers.ActivationTokenMapper;
import com.fabrica.authentication.infrastructure.database.entities.mappers.UserEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.ActivationTokenJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ActivationTokenRepositoryImpl
  implements ActivationTokenRepositoryPort
{

  private final ActivationTokenJpaRepository tokenRepo;
  private final ActivationTokenMapper tokenMapper;
  private final UserEntityMapper userMapper;

  @Override
  public void save(ActivationToken token, User user) {
    var userEntity = userMapper.toEntity(user);
    var tokenEntity = tokenMapper.mapToEntity(token, userEntity);
    log.info("Guardando token: {}", tokenEntity);
    tokenRepo.save(tokenEntity);
  }

  @Override
  public void invalidateAllByUserEmail(String email) {
    tokenRepo.invalidateAllByUserEmail(email);
  }

  @Override
  public Optional<ActivationToken> findLastByUserEmail(String email) {
    var tokenEntity = tokenRepo.findLastByUserEmail(email);
    return tokenEntity.map(tokenMapper::mapToActivationToken);
  }

  @Override
  public void increaseAttemptsByOne(UUID id) {
    tokenRepo.increaseAttemptsByOne(id);
  }
}
