package com.fabricaescuela.tasks.acceptance.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fabricaescuela.tasks.acceptance.support.ApiClient;
import com.fabricaescuela.tasks.acceptance.support.TestContext;
import com.fabricaescuela.tasks.acceptance.support.TestDataFactory;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

public class EliminarTareaSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;

  @Cuando("se elimina la tarea {string}")
  public void eliminarTarea(String taskName) {
    UUID taskId = context.get("task:" + taskName);
    context.setLastResponse(api.deleteTask(taskId));
  }

  @Cuando("se elimina una tarea que no existe")
  public void eliminarTareaInexistente() {
    context.setLastResponse(api.deleteTask(UUID.randomUUID()));
  }

  @Entonces("el sistema confirma la eliminación de la tarea")
  public void confirmaEliminacion() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status == 200 || status == 204,
        "Se esperaba 200 o 204, se obtuvo " + status + " body=" + context.getLastResponse().body());
  }

  @Y("la tarea {string} ya no existe en {string}")
  public void tareaNoExiste(String taskName, String homeName) {
    UUID homeId = context.get("home:" + homeName);
    boolean exists = data.tasks().findAll().stream()
        .anyMatch(t -> taskName.equals(t.getName()) && homeId.equals(t.getHomeId()));
    assertTrue(!exists, "La tarea '" + taskName + "' aún existe en " + homeName);
  }

  @Entonces("el sistema rechaza la eliminación de la tarea")
  public void rechazaEliminacion() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba 4xx, se obtuvo " + status + " body=" + context.getLastResponse().body());
  }
}
