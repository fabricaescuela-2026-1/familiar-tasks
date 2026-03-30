package com.fabricaescuela.tasks.infraestructure.database;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {
}
