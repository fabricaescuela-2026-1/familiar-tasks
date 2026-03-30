package com.fabricaescuela.tasks.infraestructure.database;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.fabricaescuela.tasks.domain.exceptions.PriorityNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.StatusNotFoundException;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.domain.ports.out.TaskRepositoryPort;
import com.fabricaescuela.tasks.infraestructure.database.mappers.TaskEntityMapper;

@Repository
public class TaskRepositoryAdapter implements TaskRepositoryPort {
  private final TaskJpaRepository taskRepository;
  private final PriorityJpaRepository priorityRepository;
  private final StatusJpaRepository statusRepository;

  public TaskRepositoryAdapter(TaskJpaRepository taskJpaRepository, PriorityJpaRepository priorityJpaRepository,
      StatusJpaRepository statusJpaRepository) {
    this.taskRepository = taskJpaRepository;
    this.priorityRepository = priorityJpaRepository;
    this.statusRepository = statusJpaRepository;
  }

  @Override
  public Task save(Task task) {
    var taskEntity = TaskEntityMapper.toEntity(task);
    var priorityEntity = priorityRepository.findByName(task.getPriority())
        .orElseThrow(() -> new PriorityNotFoundException(task.getPriority()));
    var statusEntity = statusRepository.findByName(task.getStatus())
        .orElseThrow(() -> new StatusNotFoundException(task.getStatus()));
    taskEntity.setPriority(priorityEntity);
    taskEntity.setStatus(statusEntity);
    taskEntity.setTaskId(UUID.randomUUID());
    taskEntity.setCreatedAt(LocalDateTime.now());

    taskEntity = taskRepository.save(taskEntity);
    return TaskEntityMapper.toDomain(taskEntity);
  }

  @Override
  public List<Task> findAll() {
    var tasks = taskRepository.findAll();
    return tasks.stream().map(TaskEntityMapper::toDomain).toList();
  }

}
