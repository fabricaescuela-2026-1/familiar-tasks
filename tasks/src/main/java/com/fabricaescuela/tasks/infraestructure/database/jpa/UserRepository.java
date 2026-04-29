package com.fabricaescuela.tasks.infraestructure.database.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findUserEntityByUsername(String username);
    
    Optional<UserEntity> findByEmail(String email);

}
