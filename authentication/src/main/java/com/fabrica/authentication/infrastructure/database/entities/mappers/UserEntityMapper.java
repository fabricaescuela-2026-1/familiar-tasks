package com.fabrica.authentication.infrastructure.database.entities.mappers;

import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

  public UserEntity toEntity(User user) {
    return UserEntity.builder()
      .userId(user.getUserId())
      .email(user.getEmail())
      .name(user.getName())
      .lastname(user.getLastname())
      .passwordHash(user.getPasswordHash())
      .isActive(user.isActive())
      .createdAt(user.getCreatedAt())
      .build();
  }

  public User toDomain(UserEntity entity) {
    return User.builder()
      .userId(entity.getUserId())
      .email(entity.getEmail())
      .name(entity.getName())
      .lastname(entity.getLastname())
      .passwordHash(entity.getPasswordHash())
      .isActive(entity.getIsActive())
      .createdAt(entity.getCreatedAt())
      .build();
  }
}
