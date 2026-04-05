package com.fabricaescuela.tasks.infraestructure.adapter.out;

import com.fabricaescuela.tasks.infraestructure.presentation.dtos.MemberHomeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Component
public class UserValidationAdapter implements com.fabricaescuela.tasks.domain.ports.out.UserValidationPort {
    private final RestTemplate restTemplate;

    public UserValidationAdapter(
            RestTemplate restClient, RestTemplate restTemplate
    )
    {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean validateUserInHome(UUID guestId, UUID homeId) {
        String url = UriComponentsBuilder.fromUriString("http://localhost:8080/get/memberHome")
                .queryParam("personId", guestId)
                .queryParam("homeId", homeId)
                .toUriString();

        return restTemplate.getForObject(url, MemberHomeDTO.class) != null;
    }
}



