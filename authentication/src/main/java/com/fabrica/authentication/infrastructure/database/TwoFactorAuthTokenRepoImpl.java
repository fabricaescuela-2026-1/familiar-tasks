package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.model.TwoFactorAuthToken;
import com.fabrica.authentication.domain.ports.out.TwoFactorAuthTokenRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.mappers.TwoFactorAuthTokenEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.TwoFactorAuthTokenJpaRepo;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TwoFactorAuthTokenRepoImpl
  implements TwoFactorAuthTokenRepositoryPort
{

  private final TwoFactorAuthTokenJpaRepo authTokenRepo;
  private final TwoFactorAuthTokenEntityMapper authTokenMapper;

  @Override
  public void invalidateAllByUserEmail(String email) {
    authTokenRepo.invalidateAllByUserEmail(email);
  }

  @Override
  public void save(TwoFactorAuthToken token) {
    var entity = authTokenMapper.toEntity(token);
    authTokenRepo.save(entity);
  }

  @Override
  public Optional<TwoFactorAuthToken> findLastByUserEmail(String email) {
    var entity = authTokenRepo.findLastByUserEmail(email);
    return entity.map(authTokenMapper::toDomain);
  }

  @Override
  public void increaseAttemptsByOne(UUID id) {
    authTokenRepo.increaseAttemptsByOne(id);
  }
}
