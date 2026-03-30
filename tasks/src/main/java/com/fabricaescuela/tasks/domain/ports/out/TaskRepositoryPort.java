package com.fabricaescuela.tasks.domain.ports.out;

import java.util.List;

import com.fabricaescuela.tasks.domain.model.Task;

public interface TaskRepositoryPort {
  Task save(Task task);

  List<Task> findAll();
}
