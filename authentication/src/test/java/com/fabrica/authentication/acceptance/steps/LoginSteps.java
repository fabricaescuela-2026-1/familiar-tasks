package com.fabrica.authentication.acceptance.steps;

import com.fabrica.authentication.acceptance.support.ApiClient;
import com.fabrica.authentication.acceptance.support.TestContext;
import com.fabrica.authentication.acceptance.support.TestUserFactory;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private UserJpaRepository userRepo;
  @Autowired private TestUserFactory factory;

  @Dado("que existe un usuario con correo {string} y contraseña {string}")
  public void existeUsuarioConCorreoYContrasena(String correo, String password) {
    factory.createUser(correo, password);
    context.put("ultimoUsuarioCorreo", correo);
  }

  @Dado("que no existe ningún usuario con correo {string}")
  public void noExisteUsuarioConCorreoLogin(String correo) {
    assertTrue(userRepo.findByEmail(correo).isEmpty());
  }

  @Cuando("solicita iniciar sesión con correo {string} y contraseña {string}")
  public void solicitaIniciarSesion(String correo, String password) {
    context.setLastResponse(api.postLogin(correo, password));
  }

  @Entonces("el sistema autentica al usuario")
  public void autenticaUsuario() {
    assertEquals(200, context.getLastResponse().statusCode());
  }

  @Y("devuelve un token de acceso válido")
  public void devuelveTokenAccesoValido() {
    String accessToken = context.getLastResponse().jsonString("accessToken");
    assertNotNull(accessToken);
    assertTrue(accessToken.length() > 10);
    context.put("accessToken", accessToken);
  }

  @Y("devuelve un token de refresco")
  public void devuelveTokenRefresco() {
    String refreshToken = context.getLastResponse().jsonString("refreshToken");
    assertNotNull(refreshToken);
    assertTrue(refreshToken.length() > 10);
    context.put("refreshToken", refreshToken);
  }

  @Entonces("el sistema rechaza el inicio de sesión")
  public void rechazaInicioSesion() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba estado 4xx, se obtuvo " + status);
  }

  @Y("la respuesta indica que las credenciales son incorrectas")
  public void indicaCredencialesIncorrectas() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("credencial") || body.contains("credential")
            || body.contains("invalid") || body.contains("not found"),
        "Se esperaba mensaje de credenciales inválidas, se obtuvo: " + body);
  }
}
