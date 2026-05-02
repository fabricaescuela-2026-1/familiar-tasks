package com.fabricaescuela.tasks.infraestructure.database.mappers;

import com.fabricaescuela.tasks.domain.model.User;
import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;

public class UserEntityMapper {

    private UserEntityMapper() {}

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .userId(entity.getUserId())
                .name(entity.getName())
                .lastname(entity.getLastname())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }


    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
