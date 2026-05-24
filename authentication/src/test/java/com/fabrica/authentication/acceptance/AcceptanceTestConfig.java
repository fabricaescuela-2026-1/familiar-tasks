package com.fabrica.authentication.acceptance;

import com.azure.storage.queue.QueueClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class AcceptanceTestConfig {

  @Bean
  @Primary
  @Qualifier("queueHomeMember")
  public QueueClient queueHomeMemberMock() {
    return mock(QueueClient.class);
  }

  @Bean
  @Primary
  @Qualifier("queueTasks")
  public QueueClient queueTasksMock() {
    return mock(QueueClient.class);
  }

  @Bean(name = "securityFilterChain")
  public SecurityFilterChain acceptanceSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(c -> c.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }
}
