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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/task")
@Tag(name = "Task", description = "Operations for managing tasks")
public class TaskController {
  private final TaskUseCasePort service;

  public TaskController(TaskUseCasePort service) {
    this.service = service;
  }

  @PostMapping("/create")
  @Operation(summary = "Create a new task", description = "Creates a new task with the provided details", method = "POST")
  public ResponseEntity<ResponseTask> createTask(@RequestBody RequestTask request) {
    var task = service.create(RequestTaskMapper.toDomain(request));
    return ResponseEntity.ok(ResponseTaskMapper.toResponse(task));
  }

  @PutMapping("/update/{id}")
  @Operation(summary = "Update an existing task", description = "Updates the details of an existing task identified by its ID", method = "PUT")
  public ResponseEntity<ResponseTask> updateTask(@PathVariable UUID id, @RequestBody RequestTask request) {
    var task = service.update(id, RequestTaskMapper.toDomain(request));
    return ResponseEntity.ok(ResponseTaskMapper.toResponse(task));
  }

  @DeleteMapping("/delete/{id}")
  @Operation(summary = "Delete a task", description = "Deletes a task identified by its ID", method = "DELETE")
  public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/all")
  @Operation(summary = "Find all tasks", description = "Retrieves a list of all tasks", method = "GET")
  public ResponseEntity<List<ResponseTask>> findAll() {
    var tasks = service.findAll().stream().map(ResponseTaskMapper::toResponse).toList();
    return ResponseEntity.ok(tasks);
  }
}
