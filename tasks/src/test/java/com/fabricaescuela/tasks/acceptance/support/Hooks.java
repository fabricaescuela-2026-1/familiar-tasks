package com.fabricaescuela.tasks.acceptance.support;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.fabricaescuela.tasks.domain.ports.out.UserValidationPort;

import io.cucumber.java.Before;

public class Hooks {

  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;
  @Autowired private UserValidationPort userValidationPort;

  @Before
  public void resetState() {
    data.tasks().deleteAll();
    data.priorities().deleteAll();
    data.statuses().deleteAll();
    data.guests().deleteAll();
    Mockito.reset(userValidationPort);
    context.reset();
  }
}
