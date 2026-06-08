package com.fabricaescuela.tasks.acceptance.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fabricaescuela.tasks.acceptance.support.ApiClient;
import com.fabricaescuela.tasks.acceptance.support.TestContext;
import com.fabricaescuela.tasks.acceptance.support.TestDataFactory;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

public class CrearTareaSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;

  @Cuando("se crea la tarea {string} con descripción {string} prioridad {string} estado {string} para {string} en {string} con vencimiento en {int} días")
  public void crearTareaParaMiembro(String name, String description, String priority, String status,
                                    String email, String homeName, int daysOffset) {
    UUID guestId = context.get("guest:" + email);
    UUID homeId = context.get("home:" + homeName);
    LocalDateTime base = daysOffset < 0
        ? LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0, 0)
        : LocalDateTime.of(2099, Month.JANUARY, 1, 10, 0, 0);
    String deadline = base.plusDays(daysOffset).toString();
    context.setLastResponse(api.postCreateTask(name, description, status, priority, homeId, guestId, deadline));
  }

  @Cuando("se crea la tarea {string} con descripción {string} prioridad {string} estado {string} para un usuario que no pertenece al hogar {string} con vencimiento en {int} días")
  public void crearTareaParaUsuarioForaneo(String name, String description, String priority, String status,
                                           String homeName, int daysOffset) {
    UUID guestId = UUID.randomUUID();
    UUID homeId = context.get("home:" + homeName);
    LocalDateTime base = daysOffset < 0
        ? LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0, 0)
        : LocalDateTime.of(2099, Month.JANUARY, 1, 10, 0, 0);
    String deadline = base.plusDays(daysOffset).toString();
    context.setLastResponse(api.postCreateTask(name, description, status, priority, homeId, guestId, deadline));
  }

  @Entonces("el sistema confirma la creación de la tarea")
  public void sistemaConfirmaCreacion() {
    assertEquals(200, context.getLastResponse().statusCode(),
        "Se esperaba 200, body=" + context.getLastResponse().body());
  }

  @Y("la tarea {string} queda registrada en {string}")
  public void tareaQuedaRegistrada(String name, String homeName) {
    UUID homeId = context.get("home:" + homeName);
    boolean exists = data.tasks().findAll().stream()
        .anyMatch(t -> name.equals(t.getName()) && homeId.equals(t.getHomeId()));
    assertTrue(exists, "La tarea '" + name + "' no fue persistida en " + homeName);
  }

  @Entonces("el sistema rechaza la creación de la tarea")
  public void sistemaRechazaCreacion() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba 4xx, se obtuvo " + status + " body=" + context.getLastResponse().body());
  }

  @Y("la respuesta indica que el nombre de la tarea es obligatorio")
  public void indicaNombreObligatorio() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("name") || body.contains("nombre") || body.contains("required")
            || body.contains("obligator") || body.contains("blank") || body.contains("empty"),
        "Se esperaba mensaje sobre nombre obligatorio, se obtuvo: " + body);
  }

  @Y("la respuesta indica que la fecha debe ser futura")
  public void indicaFechaFutura() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("future") || body.contains("futur") || body.contains("deadline")
            || body.contains("fecha"),
        "Se esperaba mensaje sobre fecha futura, se obtuvo: " + body);
  }

  @Y("la respuesta indica que el usuario no pertenece al hogar")
  public void indicaUsuarioNoPertenece() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("user not found") || body.contains("not found in the specified home")
            || body.contains("user") || body.contains("not valid") || body.contains("hogar")
            || body.contains("home"),
        "Se esperaba mensaje sobre usuario no perteneciente, se obtuvo: " + body);
  }
}
