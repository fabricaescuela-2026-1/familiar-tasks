package com.udea.usermembershipservice.acceptance.support;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.Before;

public class Hooks {

  @Autowired private TestContext context;
  @Autowired private TestDataFactory data;

  @Before
  public void resetState() {
    data.members().deleteAll();
    data.homes().deleteAll();
    data.persons().deleteAll();
    data.roles().deleteAll();
    context.reset();
  }
}
