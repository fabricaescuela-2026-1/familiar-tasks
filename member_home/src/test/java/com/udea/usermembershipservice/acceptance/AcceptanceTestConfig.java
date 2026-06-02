package com.udea.usermembershipservice.acceptance;

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
import com.udea.usermembershipservice.aplication.port.out.IAuditLogQueuePort;
import com.udea.usermembershipservice.infrastructure.adapter.in.queue.UserRegistrationMessageListener;
import com.udea.usermembershipservice.infrastructure.adapter.out.auth.AuthClient;

@TestConfiguration
public class AcceptanceTestConfig {

  @Bean(name = "auditLogSenderClientMock")
  @Primary
  public ServiceBusSenderClient auditLogSenderClientMock() {
    return mock(ServiceBusSenderClient.class);
  }

  @Bean(name = "auditLogQueuePortMock")
  @Primary
  public IAuditLogQueuePort auditLogQueuePortMock() {
    return mock(IAuditLogQueuePort.class);
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
