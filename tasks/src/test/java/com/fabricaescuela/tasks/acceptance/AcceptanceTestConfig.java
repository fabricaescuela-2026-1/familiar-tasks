package com.fabricaescuela.tasks.acceptance;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fabricaescuela.tasks.domain.ports.out.TaskAuditLogPort;
import com.fabricaescuela.tasks.domain.ports.out.UserValidationPort;
import com.fabricaescuela.tasks.infraestructure.adapter.out.AuthClient;
import com.fabricaescuela.tasks.infraestructure.adapter.out.UserRegistrationMessageListener;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        .addFilterBefore(new TestUserHeaderFilter(), BasicAuthenticationFilter.class)
        .build();
  }

  /**
   * Lee el header X-Test-Username y pone un Authentication en el SecurityContext.
   * Permite que tests de aceptacion simulen el usuario autenticado sin generar JWT real.
   */
  static class TestUserHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
      String username = request.getHeader("X-Test-Username");
      if (username != null && !username.isBlank()) {
        var auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
      try {
        chain.doFilter(request, response);
      } finally {
        SecurityContextHolder.clearContext();
      }
    }
  }
}
