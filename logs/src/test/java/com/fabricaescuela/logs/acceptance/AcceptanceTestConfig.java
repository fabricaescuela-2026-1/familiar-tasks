package com.fabricaescuela.logs.acceptance;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.fabricaescuela.logs.domain.model.Log;
import com.fabricaescuela.logs.domain.ports.out.LogRepositoryPort;

@TestConfiguration
public class AcceptanceTestConfig {

  @Bean
  @Primary
  public InMemoryLogRepository inMemoryLogRepository() {
    return new InMemoryLogRepository();
  }

  public static class InMemoryLogRepository implements LogRepositoryPort {
    private final List<Log> store = new ArrayList<>();

    @Override
    public Log save(Log log) {
      store.removeIf(existing -> existing.id().equals(log.id()));
      store.add(log);
      return log;
    }

    @Override
    public List<Log> findAll() {
      return List.copyOf(store);
    }

    public void clear() {
      store.clear();
    }
  }
}
