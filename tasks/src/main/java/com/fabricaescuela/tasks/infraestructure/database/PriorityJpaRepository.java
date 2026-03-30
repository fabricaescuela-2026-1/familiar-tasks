package com.fabricaescuela.tasks.infraestructure.database;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabricaescuela.tasks.infraestructure.database.entyties.PriorityEntity;

public interface PriorityJpaRepository extends JpaRepository<PriorityEntity, UUID> {
  Optional<PriorityEntity> findByName(String name);
}
