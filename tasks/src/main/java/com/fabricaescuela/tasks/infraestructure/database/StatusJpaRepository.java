package com.fabricaescuela.tasks.infraestructure.database;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabricaescuela.tasks.infraestructure.database.entyties.StatusEntity;

public interface StatusJpaRepository extends JpaRepository<StatusEntity, UUID> {
  Optional<StatusEntity> findByName(String name);
}
