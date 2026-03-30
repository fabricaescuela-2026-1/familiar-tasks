package com.fabricaescuela.tasks.domain.ports.in;

import com.fabricaescuela.tasks.domain.model.Task;
import java.util.List;

public interface TaskUseCasePort {
  Task create(Task task);

  List<Task> findAll();
}