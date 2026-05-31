package com.udea.usermembershipservice.acceptance.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.udea.usermembershipservice.acceptance.support.ApiClient;
import com.udea.usermembershipservice.acceptance.support.TestContext;
import com.udea.usermembershipservice.acceptance.support.TestDataFactory;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

public class CrearGrupoSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;

  @Dado("que existe el rol {string} en el sistema")
  public void existeRol(String roleName) {
    data.ensureRole(roleName);
  }

  @Y("la persona con correo {string} está registrada")
  public void personaRegistrada(String email) {
    if (data.persons().findByEmail(email.toLowerCase()).isEmpty()) {
      data.createPerson(email);
    }
  }

  @Dado("que la persona {string} no pertenece a ningún grupo familiar")
  public void personaSinGrupo(String email) {
    var person = data.persons().findByEmail(email.toLowerCase()).orElseGet(() -> data.createPerson(email));
    assertTrue(data.members().findByIdPersonId(person.getId()).isEmpty());
  }

  @Dado("que ya existe un grupo familiar con nombre {string}")
  public void existeGrupoConNombre(String homeName) {
    if (data.homes().findByNameIgnoreCase(homeName).isEmpty()) {
      data.createHome(homeName);
    }
  }

  @Cuando("{string} solicita crear el grupo familiar con nombre {string}")
  public void solicitaCrearGrupo(String email, String homeName) {
    context.setLastResponse(api.postRegisterHome(homeName, email));
  }

  @Entonces("el sistema crea el grupo {string}")
  public void sistemaCreaGrupo(String homeName) {
    assertEquals(200, context.getLastResponse().statusCode(),
        "Se esperaba 200, body=" + context.getLastResponse().body());
    assertTrue(data.homes().findByNameIgnoreCase(homeName).isPresent(),
        "El grupo " + homeName + " no fue persistido");
  }

  @Y("la persona {string} queda registrada como administrador del grupo {string}")
  public void personaEsAdmin(String email, String homeName) {
    PersonJpaEntity person = data.persons().findByEmail(email.toLowerCase()).orElseThrow();
    HomeJpaEntity home = data.homes().findByNameIgnoreCase(homeName).orElseThrow();
    RoleJpaEntity adminRole = data.roles().findByNameIgnoreCase("Administrador").orElseThrow();
    var link = data.members().findByIdPersonIdAndIdHomeId(person.getId(), home.getId()).orElseThrow();
    assertEquals(adminRole.getId(), link.getRoleId(),
        "La persona no quedó como administrador");
  }

  @Entonces("el sistema rechaza la creación del grupo")
  public void rechazaCreacionGrupo() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba 4xx, se obtuvo " + status + " body=" + context.getLastResponse().body());
  }

  @Y("la respuesta indica que el nombre del grupo es obligatorio")
  public void indicaNombreObligatorio() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("nombre") || body.contains("name") || body.contains("obligator")
            || body.contains("requir") || body.contains("blank") || body.contains("empty"),
        "Se esperaba mensaje sobre nombre obligatorio, se obtuvo: " + body);
  }

  @Y("la respuesta indica que el nombre del grupo ya está en uso")
  public void indicaNombreEnUso() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("exist") || body.contains("uso") || body.contains("duplic")
            || body.contains("already") || body.contains("nombre"),
        "Se esperaba mensaje sobre nombre duplicado, se obtuvo: " + body);
  }
}
