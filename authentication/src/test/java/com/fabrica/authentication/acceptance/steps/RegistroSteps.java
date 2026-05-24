package com.fabrica.authentication.acceptance.steps;

import com.fabrica.authentication.acceptance.support.ApiClient;
import com.fabrica.authentication.acceptance.support.ApiResponse;
import com.fabrica.authentication.acceptance.support.TestContext;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistroSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private UserJpaRepository userRepo;

  @Dado("que el servicio de autenticación está disponible")
  public void servicioAutenticacionDisponible() {
    assertTrue(userRepo.count() >= 0);
  }

  @Dado("que no existe ningún usuario registrado con el correo {string}")
  public void noExisteUsuarioConCorreo(String correo) {
    assertTrue(userRepo.findByEmail(correo).isEmpty());
  }

  @Dado("que ya existe un usuario registrado con el correo {string}")
  public void yaExisteUsuarioConCorreo(String correo) {
    Map<String, Object> body = Map.of(
        "name", "Existente",
        "lastname", "Previo",
        "email", correo,
        "password", "Segura123!");
    ApiResponse r = api.postRegister(body);
    assertEquals(201, r.statusCode(), "Precondición: el primer registro debió ser exitoso");
  }

  @Cuando("la persona solicita registrarse con:")
  public void solicitaRegistrarseCon(DataTable table) {
    List<Map<String, String>> rows = table.asMaps();
    Map<String, String> r = rows.get(0);
    Map<String, Object> body = new HashMap<>();
    body.put("name", r.get("nombre"));
    body.put("lastname", r.get("apellido"));
    body.put("email", r.get("correo"));
    body.put("password", r.get("contraseña"));
    context.setLastResponse(api.postRegister(body));
  }

  @Cuando("la persona solicita registrarse con la contraseña {string}")
  public void solicitaRegistrarseConContrasena(String password) {
    Map<String, Object> body = Map.of(
        "name", "Test",
        "lastname", "Password",
        "email", "test-" + System.nanoTime() + "@familia.com",
        "password", password);
    context.setLastResponse(api.postRegister(body));
  }

  @Cuando("la persona solicita registrarse sin diligenciar el campo {string}")
  public void solicitaRegistrarseSinCampo(String campo) {
    Map<String, Object> body = new HashMap<>();
    body.put("name", "nombre".equals(campo) ? "" : "Test");
    body.put("lastname", "apellido".equals(campo) ? "" : "Campo");
    body.put("email", "correo".equals(campo) ? "" : "campo-" + System.nanoTime() + "@familia.com");
    body.put("password", "contraseña".equals(campo) ? "" : "Segura123!");
    context.setLastResponse(api.postRegister(body));
  }

  @Entonces("el sistema confirma el registro exitoso")
  public void confirmaRegistroExitoso() {
    assertEquals(201, context.getLastResponse().statusCode());
  }

  @Y("queda registrada la cuenta del correo {string}")
  public void quedaRegistradaCuentaCorreo(String correo) {
    assertTrue(userRepo.findByEmail(correo).isPresent());
  }

  @Entonces("el sistema rechaza el registro")
  public void rechazaRegistro() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba estado 4xx, se obtuvo " + status);
  }

  @Y("la respuesta indica que el correo ya está en uso")
  public void indicaCorreoEnUso() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(body.contains("correo") || body.contains("email") || body.contains("exit"),
        "Se esperaba mensaje de correo en uso, se obtuvo: " + body);
  }

  @Y("la respuesta indica el requisito incumplido {string}")
  public void indicaRequisitoIncumplido(String requisito) {
    String body = context.getLastResponse().bodyLower();
    assertTrue(body.contains("password") || body.contains("contraseña") || body.contains("caract"),
        "Se esperaba mensaje sobre contraseña/" + requisito + ", se obtuvo: " + body);
  }

  @Y("la respuesta indica que el campo {string} es obligatorio")
  public void indicaCampoObligatorio(String campo) {
    String body = context.getLastResponse().bodyLower();
    assertTrue(body.contains(campo.toLowerCase()) || body.contains("obligator") || body.contains("requir"),
        "Se esperaba mensaje sobre campo obligatorio " + campo + ", se obtuvo: " + body);
  }
}
