package com.fabricaescuela.tasks.domain.ports.out;

import java.util.List;
import java.util.UUID;

import java.util.Optional;

import com.fabricaescuela.tasks.application.dto.TaskSearchCriteria;
import com.fabricaescuela.tasks.domain.model.Task;

public interface TaskRepositoryPort {
  Task save(Task task);

  Task update(UUID taskId, Task task);

  Task updateStatus(UUID taskId, String newStatus);

  Optional<Task> findById(UUID taskId);

  void delete(UUID taskId);

  List<Task> findAll();

  List<Task> search(TaskSearchCriteria criteria);
}
