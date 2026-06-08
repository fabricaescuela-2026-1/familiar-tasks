package com.fabricaescuela.tasks.acceptance.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

public class CambiarEstadoTareaSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;

  @Y("existe el estado {string} en el sistema de tareas")
  public void existeEstado(String name) {
    data.ensureStatus(name);
  }

  @Dado("que {string} tiene la tarea {string} en estado {string} en {string}")
  public void asignarTareaConEstado(String email, String taskName, String statusName, String homeName) {
    UUID guestId = context.get("guest:" + email);
    UUID homeId = context.get("home:" + homeName);
    StatusEntity status = data.ensureStatus(statusName);
    PriorityEntity priority = data.ensurePriority("MEDIA");
    TaskEntity created = data.createTask(taskName, "Descripción de prueba", status, priority,
        homeId, guestId, LocalDateTime.of(2099, 1, 4, 10, 0, 0));
    context.put("task:" + taskName, created.getTaskId());
  }

  @Cuando("{string} cambia el estado de la tarea {string} a {string}")
  public void cambiarEstado(String email, String taskName, String newStatus) {
    UUID taskId = context.get("task:" + taskName);
    context.setLastResponse(api.patchChangeStatus(taskId, newStatus, email));
  }

  @Entonces("el sistema confirma el cambio de estado")
  public void confirmaCambio() {
    assertEquals(200, context.getLastResponse().statusCode(),
        "Se esperaba 200, body=" + context.getLastResponse().body());
  }

  @Entonces("el sistema rechaza el cambio de estado por falta de permisos")
  public void rechazaPorPermisos() {
    assertEquals(403, context.getLastResponse().statusCode(),
        "Se esperaba 403, body=" + context.getLastResponse().body());
  }

  @Y("la tarea {string} aparece en estado {string} en el listado")
  public void verificarEstado(String taskName, String expectedStatus) {
    UUID taskId = context.get("task:" + taskName);
    TaskEntity persisted = data.tasks().findById(taskId).orElseThrow();
    assertEquals(expectedStatus, persisted.getStatus().getName());
  }

  @Y("la tarea {string} sigue en estado {string} en el listado")
  public void sigueEnEstado(String taskName, String expectedStatus) {
    verificarEstado(taskName, expectedStatus);
  }
}
