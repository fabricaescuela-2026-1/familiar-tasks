package com.fabricaescuela.tasks.infraestructure.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.tasks.application.dto.TaskSearchCriteria;
import com.fabricaescuela.tasks.domain.ports.in.TaskUseCasePort;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ChangeStatusRequest;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.RequestTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ResponseTask;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers.RequestTaskMapper;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.mappers.ResponseTaskMapper;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/task")
@Tag(name = "Task", description = "Operations for managing tasks")
//@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class TaskController {
  private final TaskUseCasePort service;

  public TaskController(TaskUseCasePort service) {
    this.service = service;
  }

  @PostMapping("/create")
//@SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Create a new task", description = "Creates a new task with the provided details", method = "POST")
  public ResponseEntity<ResponseTask> createTask(@RequestBody RequestTask request) {
    var task = service.create(RequestTaskMapper.toDomain(request));
    return ResponseEntity.ok(ResponseTaskMapper.toResponse(task));
  }

  @PutMapping("/update/{id}")
//@SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Update an existing task", description = "Updates the details of an existing task identified by its ID", method = "PUT")
  public ResponseEntity<ResponseTask> updateTask(@PathVariable UUID id, @RequestBody RequestTask request) {
    var task = service.update(id, RequestTaskMapper.toDomain(request));
    return ResponseEntity.ok(ResponseTaskMapper.toResponse(task));
  }

  @DeleteMapping("/delete/{id}")
//@SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Delete a task", description = "Deletes a task identified by its ID", method = "DELETE")
  public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/status")
//@SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Cambiar el estado de una tarea",
      description = "Solo el miembro asignado (guestId) puede cambiar el estado. HU20",
      method = "PATCH")
  public ResponseEntity<ResponseTask> changeStatus(@PathVariable UUID id,
                                                   @RequestBody ChangeStatusRequest request,
                                                   Authentication authentication) {
    String username = authentication != null ? authentication.getName() : null;
    var task = service.changeStatus(id, request.status(), username);
    return ResponseEntity.ok(ResponseTaskMapper.toResponse(task));
  }

  @GetMapping("/all")
//@SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Find all tasks", description = "Retrieves a list of all tasks", method = "GET")
  public ResponseEntity<List<ResponseTask>> findAll() {
    var tasks = service.findAll().stream().map(ResponseTaskMapper::toResponse).toList();
    return ResponseEntity.ok(tasks);
  }

  @GetMapping("/search")
//@SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Buscar tareas por nombre o descripción", description = "Búsqueda parcial case-insensitive; sin keyword retorna todas", method = "GET")
  public ResponseEntity<List<ResponseTask>> search(@RequestParam(required = false) String keyword) {
    var tareas = service.search(new TaskSearchCriteria(keyword)).stream()
        .map(ResponseTaskMapper::toResponse).toList();
    return ResponseEntity.ok(tareas);
  }
}
