package com.fabricaescuela.tasks.domain.ports.in;

import com.fabricaescuela.tasks.domain.model.Task;
import java.util.List;
import java.util.UUID;

public interface TaskUseCasePort {
  Task create(Task task);

  Task update(UUID taskId, Task task);

  void delete(UUID taskId);

  List<Task> findAll();
}