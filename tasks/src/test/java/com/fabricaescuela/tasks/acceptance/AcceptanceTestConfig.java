package com.fabricaescuela.tasks.acceptance;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import com.fabricaescuela.tasks.domain.ports.out.UserValidationPort;
import com.fabricaescuela.tasks.infraestructure.adapter.out.AuthClient;
import com.fabricaescuela.tasks.infraestructure.adapter.out.UserRegistrationMessageListener;

@TestConfiguration
public class AcceptanceTestConfig {

  @Bean(name = "auditLogSenderClientMock")
  @Primary
  public ServiceBusSenderClient auditLogSenderClientMock() {
    return mock(ServiceBusSenderClient.class);
  }

  @Bean(name = "taskAuditLogPortMock")
  @Primary
  public TaskAuditLogPort taskAuditLogPortMock() {
    return mock(TaskAuditLogPort.class);
  }

  @Bean
  @Primary
  public UserValidationPort userValidationPort() {
    return mock(UserValidationPort.class);
  }

  @Bean
  @Primary
  public AuthClient authClient() {
    return mock(AuthClient.class);
  }

  @Bean(name = "userRegistrationMessageListener")
  public UserRegistrationMessageListener userRegistrationMessageListener() {
    return mock(UserRegistrationMessageListener.class);
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain acceptanceSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(c -> c.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }
}
