package com.fabricaescuela.tasks.acceptance.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fabricaescuela.tasks.acceptance.support.ApiClient;
import com.fabricaescuela.tasks.acceptance.support.TestContext;
import com.fabricaescuela.tasks.acceptance.support.TestDataFactory;
import com.fabricaescuela.tasks.infraestructure.database.entyties.PriorityEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.StatusEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

public class EditarTareaSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;

  @Y("existe la tarea {string} asignada a {string} en {string}")
  public void existeTareaAsignada(String taskName, String email, String homeName) {
    UUID guestId = context.get("guest:" + email);
    UUID homeId = context.get("home:" + homeName);
    StatusEntity status = data.ensureStatus("PENDIENTE");
    PriorityEntity priority = data.ensurePriority("MEDIA");
    TaskEntity created = data.createTask(taskName, "Descripción de prueba", status, priority,
        homeId, guestId, LocalDateTime.now().plusDays(7));
    context.put("task:" + taskName, created.getTaskId());
  }

  @Cuando("se edita la tarea {string} cambiando el nombre a {string} y el estado a {string}")
  public void editarTarea(String taskName, String newName, String newStatus) {
    UUID taskId = context.get("task:" + taskName);
    TaskEntity existing = data.tasks().findById(taskId).orElseThrow();
    UUID guestId = existing.getGuestId();
    UUID homeId = existing.getHomeId();
    String deadline = LocalDateTime.now().plusDays(10).toString();
    context.setLastResponse(api.putUpdateTask(
        taskId,
        newName,
        existing.getDescription(),
        newStatus,
        existing.getPriority().getName(),
        homeId,
        guestId,
        deadline));
  }

  @Entonces("el sistema confirma la actualización de la tarea")
  public void confirmaActualizacion() {
    assertEquals(200, context.getLastResponse().statusCode(),
        "Se esperaba 200, body=" + context.getLastResponse().body());
  }

  @Entonces("el sistema rechaza la actualización de la tarea")
  public void rechazaActualizacion() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba 4xx, se obtuvo " + status + " body=" + context.getLastResponse().body());
  }
}
