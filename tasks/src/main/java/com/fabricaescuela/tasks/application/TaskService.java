package com.fabricaescuela.tasks.application;

import java.util.List;
import java.util.UUID;

import com.fabricaescuela.tasks.application.dto.TaskSearchCriteria;
import com.fabricaescuela.tasks.domain.exceptions.ForbiddenTaskOperationException;
import com.fabricaescuela.tasks.domain.exceptions.TaskNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.UserNotValidException;
import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import com.fabricaescuela.tasks.domain.ports.out.UserLookupPort;
import com.fabricaescuela.tasks.domain.ports.out.UserValidationPort;
import org.springframework.stereotype.Service;

import com.fabricaescuela.tasks.domain.TaskValidator;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.domain.ports.in.TaskUseCasePort;
import com.fabricaescuela.tasks.domain.ports.out.TaskRepositoryPort;

@Service
public class TaskService implements TaskUseCasePort {
  private final TaskRepositoryPort repository;
  private final UserValidationPort userValidation;
  private final TaskAuditLogPort auditLog;
  private final UserLookupPort userLookup;

  public TaskService(TaskRepositoryPort repository, UserValidationPort userValidation,
                     TaskAuditLogPort auditLog, UserLookupPort userLookup) {
    this.repository = repository;
    this.userValidation = userValidation;
    this.auditLog = auditLog;
    this.userLookup = userLookup;
  }

  @Override
  public Task create(Task task) {
    if (task.getStatus() == null) {
      task.setStatus("PENDIENTE");
    }
    TaskValidator.validate(task);
    TaskValidator.validateUserIds(task);
    boolean isValid = userValidation.validateUserInHome(
        task.getGuestId(),
        task.getHomeId());

    if (!isValid) {
      throw new UserNotValidException(
          "User not found in the specified home");
    }
    Task saved = repository.save(task);
    auditLog.publishTaskCreated(saved.getGuestId(), saved.getTaskId());
    return saved;
  }

  @Override
  public Task update(UUID taskId, Task task) {
    TaskValidator.validate(task);
    TaskValidator.validateUserIds(task);
    boolean isValid = userValidation.validateUserInHome(
        task.getGuestId(),
        task.getHomeId());

    if (!isValid) {
      throw new UserNotValidException(
          "User not found in the specified home");
    }
    return repository.update(taskId, task);
  }

  @Override
  public void delete(UUID taskId) {
    repository.delete(taskId);
  }

  @Override
  public Task changeStatus(UUID taskId, String newStatus, String username) {
    if (newStatus == null || newStatus.isBlank()) {
      throw new IllegalArgumentException("El nuevo estado es obligatorio");
    }
    UUID userId = userLookup.findUserIdByUsername(username)
        .orElseThrow(() -> new ForbiddenTaskOperationException(
            "Usuario no autenticado o no encontrado"));
    Task task = repository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));
    if (!userId.equals(task.getGuestId())) {
      throw new ForbiddenTaskOperationException(
          "No tienes permiso para modificar esta tarea");
    }
    Task updated = repository.updateStatus(taskId, newStatus);
    auditLog.publishTaskStatusChanged(userId, taskId, newStatus);
    return updated;
  }

  @Override
  public List<Task> findAll() {
    return repository.findAll();
  }

  @Override
  public List<Task> search(TaskSearchCriteria criteria) {
    return repository.search(criteria);
  }
}
