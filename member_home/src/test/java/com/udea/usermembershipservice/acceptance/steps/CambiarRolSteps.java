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

public class CambiarRolSteps {

  @Autowired private ApiClient api;
  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;

  @Dado("que existen los roles {string} y {string} en el sistema")
  public void existenRoles(String r1, String r2) {
    data.ensureRole(r1);
    data.ensureRole(r2);
  }

  @Y("existe el grupo familiar {string}")
  public void existeGrupoFamiliar(String homeName) {
    if (data.homes().findByNameIgnoreCase(homeName).isEmpty()) {
      data.createHome(homeName);
    }
  }

  @Dado("que {string} es administrador del grupo {string}")
  public void personaEsAdminDeGrupo(String email, String homeName) {
    PersonJpaEntity person = ensurePerson(email);
    HomeJpaEntity home = ensureHome(homeName);
    RoleJpaEntity adminRole = data.ensureRole("Administrador");
    if (data.members().findByIdPersonIdAndIdHomeId(person.getId(), home.getId()).isEmpty()) {
      data.linkMember(home.getId(), person.getId(), adminRole.getId());
    }
  }

  @Y("{string} es miembro del grupo {string} con rol {string}")
  public void personaEsMiembroConRol(String email, String homeName, String roleName) {
    PersonJpaEntity person = ensurePerson(email);
    HomeJpaEntity home = ensureHome(homeName);
    RoleJpaEntity role = data.ensureRole(roleName);
    if (data.members().findByIdPersonIdAndIdHomeId(person.getId(), home.getId()).isEmpty()) {
      data.linkMember(home.getId(), person.getId(), role.getId());
    }
  }

  @Dado("que {string} es miembro del grupo {string} con rol {string}")
  public void personaEsMiembroConRolDado(String email, String homeName, String roleName) {
    personaEsMiembroConRol(email, homeName, roleName);
  }

  @Dado("que {string} es el único administrador del grupo {string}")
  public void personaEsUnicoAdmin(String email, String homeName) {
    PersonJpaEntity person = ensurePerson(email);
    HomeJpaEntity home = ensureHome(homeName);
    RoleJpaEntity adminRole = data.ensureRole("Administrador");
    if (data.members().findByIdPersonIdAndIdHomeId(person.getId(), home.getId()).isEmpty()) {
      data.linkMember(home.getId(), person.getId(), adminRole.getId());
    }
    long adminCount = data.members().findAllByIdHomeId(home.getId()).stream()
        .filter(m -> m.getRoleId().equals(adminRole.getId()))
        .count();
    assertEquals(1L, adminCount, "Debe haber exactamente un administrador para este escenario");
  }

  @Cuando("{string} cambia el rol de {string} en {string} a {string}")
  public void adminCambiaRol(String emailAdmin, String emailMiembro, String homeName, String newRol) {
    context.setLastResponse(api.postUpdateRole(homeName, emailMiembro, newRol, emailAdmin));
  }

  @Cuando("{string} intenta cambiar el rol de {string} en {string} a {string}")
  public void intentaCambiarRol(String emailAdmin, String emailMiembro, String homeName, String newRol) {
    context.setLastResponse(api.postUpdateRole(homeName, emailMiembro, newRol, emailAdmin));
  }

  @Cuando("{string} intenta cambiar su propio rol en {string} a {string}")
  public void intentaCambiarPropioRol(String email, String homeName, String newRol) {
    context.setLastResponse(api.postUpdateRole(homeName, email, newRol, email));
  }

  @Entonces("el sistema actualiza el rol de {string} a {string} en el grupo {string}")
  public void sistemaActualizaRol(String email, String newRol, String homeName) {
    assertEquals(200, context.getLastResponse().statusCode(),
        "Se esperaba 200, body=" + context.getLastResponse().body());
    PersonJpaEntity person = ensurePerson(email);
    HomeJpaEntity home = ensureHome(homeName);
    RoleJpaEntity role = data.ensureRole(newRol);
    var link = data.members().findByIdPersonIdAndIdHomeId(person.getId(), home.getId()).orElseThrow();
    assertEquals(role.getId(), link.getRoleId(), "El rol del miembro no fue actualizado");
  }

  @Entonces("el sistema rechaza el cambio de rol")
  public void rechazaCambioRol() {
    int status = context.getLastResponse().statusCode();
    assertTrue(status >= 400 && status < 500,
        "Se esperaba 4xx, se obtuvo " + status + " body=" + context.getLastResponse().body());
  }

  @Y("la respuesta indica que no tiene permisos para esa acción")
  public void indicaSinPermisos() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("admin") || body.contains("permiso") || body.contains("permission")
            || body.contains("not an admin") || body.contains("autoriza"),
        "Se esperaba mensaje sobre permisos, se obtuvo: " + body);
  }

  @Y("la respuesta indica que el grupo debe tener al menos un administrador")
  public void indicaDebeTenerAdmin() {
    String body = context.getLastResponse().bodyLower();
    assertTrue(
        body.contains("administrador") || body.contains("administrator")
            || body.contains("only admin") || body.contains("least") || body.contains("único"),
        "Se esperaba mensaje sobre único administrador, se obtuvo: " + body);
  }

  private PersonJpaEntity ensurePerson(String email) {
    return data.persons().findByEmail(email.toLowerCase()).orElseGet(() -> data.createPerson(email));
  }

  private HomeJpaEntity ensureHome(String homeName) {
    return data.homes().findByNameIgnoreCase(homeName).orElseGet(() -> data.createHome(homeName));
  }
}
