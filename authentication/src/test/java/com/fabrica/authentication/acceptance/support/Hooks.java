package com.fabrica.authentication.acceptance.support;

import com.fabrica.authentication.infrastructure.database.jpa.TokenJpaRepository;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class Hooks {

  @Autowired private TestContext context;
  @Autowired private UserJpaRepository userRepo;
  @Autowired private TokenJpaRepository tokenRepo;

  @Before
  public void resetState() {
    tokenRepo.deleteAll();
    userRepo.deleteAll();
    context.reset();
  }
}
