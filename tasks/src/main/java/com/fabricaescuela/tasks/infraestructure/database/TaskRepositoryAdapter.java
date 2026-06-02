package com.fabricaescuela.tasks.infraestructure.database;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.fabricaescuela.tasks.application.dto.TaskSearchCriteria;
import com.fabricaescuela.tasks.domain.exceptions.PriorityNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.StatusNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.TaskNotFoundException;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.domain.ports.out.TaskRepositoryPort;
import com.fabricaescuela.tasks.infraestructure.database.mappers.TaskEntityMapper;
import com.fabricaescuela.tasks.infraestructure.database.specifications.TaskSpecifications;

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
  public Task update(UUID taskId, Task task) {
    var existingTask = taskRepository.findById(taskId)
        .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

    var priorityEntity = priorityRepository.findByName(task.getPriority())
        .orElseThrow(() -> new PriorityNotFoundException(task.getPriority()));
    var statusEntity = statusRepository.findByName(task.getStatus())
        .orElseThrow(() -> new StatusNotFoundException(task.getStatus()));

    existingTask.setName(task.getName());
    existingTask.setDescription(task.getDescription());
    existingTask.setDeadline(task.getDeadline());
    existingTask.setHomeId(task.getHomeId());
    existingTask.setGuestId(task.getGuestId());
    existingTask.setPriority(priorityEntity);
    existingTask.setStatus(statusEntity);

    var updatedTask = taskRepository.save(existingTask);
    return TaskEntityMapper.toDomain(updatedTask);
  }

  @Override
  public Task updateStatus(UUID taskId, String newStatus) {
    var existingTask = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));
    var statusEntity = statusRepository.findByName(newStatus)
        .orElseThrow(() -> new StatusNotFoundException(newStatus));
    existingTask.setStatus(statusEntity);
    return TaskEntityMapper.toDomain(taskRepository.save(existingTask));
  }

  @Override
  public Optional<Task> findById(UUID taskId) {
    return taskRepository.findById(taskId).map(TaskEntityMapper::toDomain);
  }

  @Override
  public void delete(UUID taskId) {
    if (!taskRepository.existsById(taskId)) {
      throw new IllegalArgumentException("Task not found: " + taskId);
    }
    taskRepository.deleteById(taskId);
  }

  @Override
  public List<Task> findAll() {
    var tasks = taskRepository.findAll();
    return tasks.stream().map(TaskEntityMapper::toDomain).toList();
  }

  @Override
  public List<Task> search(TaskSearchCriteria criteria) {
    var spec = Specification.where(TaskSpecifications.nameOrDescriptionContains(criteria.keyword()));
    return taskRepository.findAll(spec).stream().map(TaskEntityMapper::toDomain).toList();
  }

}
