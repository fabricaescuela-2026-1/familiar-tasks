package com.fabricaescuela.tasks.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fabricaescuela.tasks.domain.TaskValidator;
import com.fabricaescuela.tasks.domain.model.Task;
import com.fabricaescuela.tasks.domain.ports.in.TaskUseCasePort;
import com.fabricaescuela.tasks.domain.ports.out.TaskRepositoryPort;

@Service
public class TaskService implements TaskUseCasePort {
  private final TaskRepositoryPort repository;

  public TaskService(TaskRepositoryPort repository) {
    this.repository = repository;
  }

  @Override
  public Task create(Task task) {
    TaskValidator.validate(task);
    return repository.save(task);
  }

  @Override
  public List<Task> findAll() {
    return repository.findAll();
  }
}
