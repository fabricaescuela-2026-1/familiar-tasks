package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.ActivationToken;
import com.fabrica.authentication.domain.ports.out.ActivationTokenRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import com.fabrica.authentication.infrastructure.database.entities.mappers.ActivationTokenMapper;
import com.fabrica.authentication.infrastructure.database.jpa.ActivationTokenJpaRepository;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ActivationTokenRepositoryImpl
  implements ActivationTokenRepositoryPort
{

  private final UserJpaRepository userRepo;
  private final ActivationTokenJpaRepository tokenRepo;
  private final ActivationTokenMapper tokenMapper;

  @Override
  public void save(ActivationToken token) {
    var userEntity = getUserEntity(token);
    var tokenEntity = tokenMapper.mapToEntity(token, userEntity);
    tokenRepo.save(tokenEntity);
  }

  private UserEntity getUserEntity(ActivationToken token) {
    var userEntity = userRepo
      .findByEmail(token.getEmail())
      .orElseThrow(UserNotFoundException::new);
    return userEntity;
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
}
