package com.fabricaescuela.tasks.domain.ports.out;

import java.util.List;
import java.util.UUID;

import com.fabricaescuela.tasks.domain.model.Task;

public interface TaskRepositoryPort {
  Task save(Task task);

  Task update(UUID taskId, Task task);

  void delete(UUID taskId);

  List<Task> findAll();
}
