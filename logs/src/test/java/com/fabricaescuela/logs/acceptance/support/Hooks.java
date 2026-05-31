package com.fabricaescuela.logs.acceptance.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.fabricaescuela.logs.acceptance.AcceptanceTestConfig.InMemoryLogRepository;

import io.cucumber.java.Before;

public class Hooks {

  @Autowired private TestContext context;
  @Autowired private InMemoryLogRepository repository;

  @Before
  public void resetState() {
    repository.clear();
    context.reset();
  }
}
