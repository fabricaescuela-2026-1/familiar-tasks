package com.fabricaescuela.tasks.application;

import java.util.List;
import java.util.UUID;

import com.fabricaescuela.tasks.domain.exceptions.UserNotValidException;
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

  public TaskService(TaskRepositoryPort repository, UserValidationPort userValidation) {
    this.repository = repository;
    this.userValidation = userValidation;
  }

  @Override
  public Task create(Task task) {
    TaskValidator.validate(task);
    TaskValidator.validateUserIds(task);
    boolean isValid = userValidation.validateUserInHome(
        task.getGuestId(),
        task.getHomeId());

    if (!isValid) {
      throw new UserNotValidException(
          "User not found in the specified home");
    }
    return repository.save(task);
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
  public List<Task> findAll() {
    return repository.findAll();
  }
}
