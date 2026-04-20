package com.fabrica.authentication.infrastructure.database;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import com.fabrica.authentication.infrastructure.database.entities.mappers.UserEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryPort {

  private final UserJpaRepository userJpaRepo;
  private final UserEntityMapper userEntityMapper;

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepo.findByEmail(email).map(userEntityMapper::toDomain);
  }

  @Override
  public User save(User user) {
    return userEntityMapper.toDomain(userJpaRepo.save(userEntityMapper.toEntity(user)));
  }

}
