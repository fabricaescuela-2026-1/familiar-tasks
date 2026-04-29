package com.fabricaescuela.tasks.infraestructure.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.tasks.domain.ports.in.TaskUseCasePort;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.RequestTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ResponseTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers.RequestTaskMapper;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers.ResponseTaskMapper;

@RestController
@RequestMapping("/task")
public class TaskController {
  private final TaskUseCasePort service;

  public TaskController(TaskUseCasePort service) {
    this.service = service;
  }

  @PostMapping("/create")
  public ResponseEntity<ResponseTask> createTask(@RequestBody RequestTask request) {
    var task = service.create(RequestTaskMapper.toDomain(request));
    return ResponseEntity.ok(ResponseTaskMapper.toResponse(task));
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<ResponseTask> updateTask(@PathVariable UUID id, @RequestBody RequestTask request) {
    var task = service.update(id, RequestTaskMapper.toDomain(request));
    return ResponseEntity.ok(ResponseTaskMapper.toResponse(task));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/all")
  public ResponseEntity<List<ResponseTask>> findAll() {
    var tasks = service.findAll().stream().map(ResponseTaskMapper::toResponse).toList();
    return ResponseEntity.ok(tasks);
  }
}
