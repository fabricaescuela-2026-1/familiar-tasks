package com.fabricaescuela.tasks.acceptance.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fabricaescuela.tasks.acceptance.support.TestContext;
import com.fabricaescuela.tasks.acceptance.support.TestDataFactory;
import com.fabricaescuela.tasks.domain.ports.out.UserValidationPort;
import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Y;

public class CommonTareasSteps {

  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;
  @Autowired private UserValidationPort userValidationPort;

  @Dado("que existen los estados {string} y {string} en el sistema de tareas")
  public void existenEstados(String s1, String s2) {
    data.ensureStatus(s1);
    data.ensureStatus(s2);
  }

  @Y("existen las prioridades {string} y {string} en el sistema de tareas")
  public void existenPrioridades(String p1, String p2) {
    data.ensurePriority(p1);
    data.ensurePriority(p2);
  }

  @Y("existe el miembro {string} en el hogar {string}")
  public void existeMiembroEnHogar(String email, String homeName) {
    UserEntity guest = data.guests().findByEmail(email).orElseGet(() -> data.createGuest(email));
    UUID existing = context.get("home:" + homeName);
    final UUID homeId = existing != null ? existing : UUID.randomUUID();
    if (existing == null) {
      context.put("home:" + homeName, homeId);
    }
    context.put("guest:" + email, guest.getUserId());
    context.put("guestHome:" + email, homeId);
    when(userValidationPort.validateUserInHome(any(UUID.class), any(UUID.class))).thenAnswer(inv -> {
      UUID g = inv.getArgument(0);
      UUID h = inv.getArgument(1);
      return guest.getUserId().equals(g) && homeId.equals(h);
    });
  }
}
