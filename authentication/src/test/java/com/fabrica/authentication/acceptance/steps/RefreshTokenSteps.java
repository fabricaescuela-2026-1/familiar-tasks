package com.fabrica.authentication.acceptance.steps;

import com.fabrica.authentication.acceptance.support.ApiClient;
import com.fabrica.authentication.acceptance.support.TestContext;
import com.fabrica.authentication.acceptance.support.TestUserFactory;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.TokenEntity;
import com.fabrica.authentication.infrastructure.database.jpa.TokenJpaRepository;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefreshTokenSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private TokenJpaRepository tokenRepo;
  @Autowired private TestUserFactory factory;

  @Dado("que el usuario tiene un token de refresco vigente")
  public void usuarioTokenRefrescoVigente() {
    User user = factory.createUser("refresh-" + System.nanoTime() + "@familia.com", "Segura123!");
    Token refresh = factory.issueValidRefreshToken(user);
    context.put("refreshToken", refresh.getTokenHash());
  }

  @Dado("que el usuario tiene un token de refresco vencido")
  public void usuarioTokenRefrescoVencido() {
    User user = factory.createUser("expired-" + System.nanoTime() + "@familia.com", "Segura123!");
    Token refresh = factory.issueValidRefreshToken(user);
    TokenEntity entity = tokenRepo.findByTokenHash(refresh.getTokenHash()).orElseThrow();
    entity.setExpirationDate(LocalDateTime.now().minusDays(1));
    tokenRepo.save(entity);
    context.put("refreshToken", refresh.getTokenHash());
  }

  @Dado("que el usuario tiene un token de acceso vencido")
  public void usuarioTokenAccesoVencido() {
    User user = factory.createUser("access-expired-" + System.nanoTime() + "@familia.com", "Segura123!");
    Token access = factory.issueValidAccessToken(user);
    TokenEntity entity = tokenRepo.findByTokenHash(access.getTokenHash()).orElseThrow();
    entity.setExpirationDate(LocalDateTime.now().minusDays(1));
    tokenRepo.save(entity);
    context.put("accessToken", access.getTokenHash());
  }

  @Cuando("solicita renovar su sesión")
  public void solicitaRenovarSesion() {
    String refreshToken = context.get("refreshToken");
    context.setLastResponse(api.postRefresh(refreshToken));
  }

  @Cuando("intenta ejecutar una acción autenticada")
  public void intentaEjecutarAccionAutenticada() {
    String accessToken = context.get("accessToken");
    context.setLastResponse(api.getToken(accessToken));
  }

  @Entonces("el sistema devuelve un nuevo token de acceso válido")
  public void devuelveNuevoTokenAcceso() {
    assertEquals(200, context.getLastResponse().statusCode());
    String nuevoAccessToken = context.getLastResponse().jsonString("accessToken");
    assertNotNull(nuevoAccessToken);
    context.put("nuevoAccessToken", nuevoAccessToken);
  }

  @Y("devuelve un nuevo token de refresco")
  public void devuelveNuevoTokenRefresco() {
    String nuevoRefreshToken = context.getLastResponse().jsonString("refreshToken");
    assertNotNull(nuevoRefreshToken);
    String anterior = context.get("refreshToken");
    assertNotEquals(anterior, nuevoRefreshToken,
        "Se esperaba un refresh token distinto al anterior");
  }

  @Entonces("el sistema rechaza la renovación")
  public void rechazaRenovacion() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba estado 4xx, se obtuvo " + status);
  }

  @Y("la respuesta indica que la sesión expiró")
  public void indicaSesionExpiro() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("expir") || body.contains("invalid") || body.contains("refresh"),
        "Se esperaba mensaje sobre sesión expirada, se obtuvo: " + body);
  }

  @Y("solicita iniciar sesión de nuevo")
  public void solicitaIniciarSesionNuevo() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status == 401 || status == 403 || status == 400,
        "Se esperaba un código que requiera nuevo login, se obtuvo " + status);
  }

  @Entonces("el sistema rechaza la acción")
  public void rechazaAccion() {
    rechazaRenovacion();
  }
}
