package com.fabrica.authentication.acceptance.steps;

import com.fabrica.authentication.acceptance.support.ApiClient;
import com.fabrica.authentication.acceptance.support.TestContext;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidacionPasswordSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;

  @Cuando("se valida la contraseña {string}")
  public void seValidaLaContrasena(String password) {
    Map<String, Object> body = Map.of(
        "name", "Validador",
        "lastname", "Password",
        "email", "validar-" + System.nanoTime() + "@familia.com",
        "password", password);
    context.setLastResponse(api.postRegister(body));
  }

  @Entonces("el sistema la acepta como válida")
  public void aceptaContrasenaValida() {
    assertEquals(201, context.getLastResponse().statusCode());
  }

  @Entonces("el sistema la rechaza")
  public void rechazaContrasena() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba estado 4xx, se obtuvo " + status);
  }

  @Cuando("se ingresa la contraseña {string} y la confirmación {string}")
  public void ingresaContrasenaYConfirmacion(String pwd, String confirm) {
    Map<String, Object> body = Map.of(
        "name", "Confirma",
        "lastname", "Password",
        "email", "confirm-" + System.nanoTime() + "@familia.com",
        "password", pwd,
        "passwordConfirmation", confirm);
    context.setLastResponse(api.postRegister(body));
  }

  @Entonces("el sistema rechaza la operación")
  public void rechazaOperacion() {
    rechazaContrasena();
  }

  @Y("la respuesta indica que las contraseñas no coinciden")
  public void indicaContrasenasNoCoinciden() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(body.contains("coincid") || body.contains("confirm") || body.contains("match"),
        "Se esperaba mensaje sobre coincidencia de contraseñas, se obtuvo: " + body);
  }
}
