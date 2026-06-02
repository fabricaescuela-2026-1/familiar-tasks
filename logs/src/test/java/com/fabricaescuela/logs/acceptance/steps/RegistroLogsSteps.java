package com.fabricaescuela.logs.acceptance.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fabricaescuela.logs.acceptance.AcceptanceTestConfig.InMemoryLogRepository;
import com.fabricaescuela.logs.acceptance.support.ApiClient;
import com.fabricaescuela.logs.acceptance.support.ApiResponse;
import com.fabricaescuela.logs.acceptance.support.TestContext;
import com.fabricaescuela.logs.domain.model.Log;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

public class RegistroLogsSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private InMemoryLogRepository repository;

  @Dado("que se han registrado los siguientes logs:")
  public void existenLogsPrevios(DataTable table) {
    List<Map<String, String>> rows = table.asMaps();
    for (Map<String, String> row : rows) {
      ApiResponse resp = api.postCreateLog(
          row.get("id"), row.get("idUser"), row.get("modifiedElement"), row.get("action"));
      assertEquals(201, resp.statusCode(),
          "No se pudo precargar log " + row.get("id") + " body=" + resp.body());
    }
  }

  @Cuando("se registra un log con id {string} usuario {string} elemento {string} acción {string}")
  public void registrarLog(String id, String idUser, String element, String action) {
    context.setLastResponse(api.postCreateLog(id, idUser, element, action));
  }

  @Cuando("se consultan todos los logs registrados")
  public void consultarLogs() {
    context.setLastResponse(api.getAllLogs());
  }

  @Entonces("el sistema confirma el registro del log")
  public void confirmaRegistro() {
    assertEquals(201, context.getLastResponse().statusCode(),
        "Se esperaba 201, body=" + context.getLastResponse().body());
  }

  @Y("la respuesta del log incluye el campo {string} con un valor no vacío")
  public void respuestaIncluyeCampo(String field) {
    String value = context.getLastResponse().jsonString(field);
    assertNotNull(value, "El campo " + field + " no está en la respuesta");
    assertTrue(!value.isBlank(), "El campo " + field + " llegó vacío");
  }

  @Entonces("el sistema devuelve {int} logs")
  public void sistemaDevuelveNLogs(int expected) {
    assertEquals(200, context.getLastResponse().statusCode());
    int actual = context.getLastResponse().json().size();
    assertEquals(expected, actual,
        "Se esperaban " + expected + " logs, se obtuvieron " + actual);
  }

  @Entonces("el sistema rechaza el registro del log")
  public void rechazaRegistro() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba 4xx, se obtuvo " + status + " body=" + context.getLastResponse().body());
  }

  @Cuando("Sofía modifica la tarea {string} con la acción {string} identificándose como {string} en el log {string}")
  public void sofiaModificaTarea(String element, String action, String idUser, String logId) {
    context.setLastResponse(api.postCreateLog(logId, idUser, element, action));
  }

  @Cuando("Sofía elimina la tarea {string} con la acción {string} identificándose como {string} en el log {string}")
  public void sofiaEliminaTarea(String element, String action, String idUser, String logId) {
    context.setLastResponse(api.postCreateLog(logId, idUser, element, action));
  }

  @Y("el log {string} registra que el usuario {string} realizó la acción {string} sobre {string}")
  public void logRegistraQueUsuarioActuaSobreElemento(String logId, String idUser, String action, String element) {
    Log log = repository.findAll().stream()
        .filter(l -> logId.equals(l.id()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("No se encontró el log " + logId));
    assertEquals(idUser, log.idUser(), "idUser no coincide en " + logId);
    assertEquals(action, log.action(), "action no coincide en " + logId);
    assertEquals(element, log.modifiedElement(), "modifiedElement no coincide en " + logId);
  }

  @Y("el log {string} tiene un timestamp asignado automáticamente")
  public void logTieneTimestamp(String logId) {
    Log log = repository.findAll().stream()
        .filter(l -> logId.equals(l.id()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("No se encontró el log " + logId));
    assertNotNull(log.timestamp(), "El log " + logId + " no tiene timestamp");
  }
}
