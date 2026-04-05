package com.fabricaescuela.tasks.infraestructure.config;

import com.fabricaescuela.tasks.infraestructure.adapter.out.UserValidationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserValidationAdapter userValidationAdapter (RestTemplate restTemplate) {
        return new UserValidationAdapter(restTemplate, restTemplate);
    }
}
