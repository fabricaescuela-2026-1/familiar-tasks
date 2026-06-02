package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.mappers.UserEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryPort {

  private final UserJpaRepository userRepo;
  private final UserEntityMapper userEntityMapper;

  @Override
  public Optional<User> findByEmail(String email) {
    return userRepo.findByEmail(email).map(userEntityMapper::toDomain);
  }

  @Override
  public User save(User user) {
    return userEntityMapper.toDomain(
      userRepo.save(userEntityMapper.toEntity(user))
    );
  }

  @Override
  public void activateUserByEmail(String email) {
    userRepo.activateUserByEmail(email);
  }
}
