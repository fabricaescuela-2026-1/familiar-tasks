package com.udea.usermembershipservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/register",
                    "/auth/**",
                    "/getUsers",
                    "/GetUserByEmail",
                    "/deleteUser",
                    "/login",
                    "/registerRole",
                    "/getRoles",
                    "/getRoleByName",
                    "/deleteRole",
                    "/registerHome",
                    "/getHomes",
                    "/getHomeByName",
                    "/deleteHome",
                    "/GetMemberHome",
                    "/save/memberHome",
                    "/delete/memberHome",
                    "/get/memberHome",
                    "/updateRole"
                ).permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }

}
