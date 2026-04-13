package com.fabricaescuela.tasks.infraestructure.adapter.out;

import com.fabricaescuela.tasks.infraestructure.presentation.dtos.MemberHomeDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidationAdapter implements com.fabricaescuela.tasks.domain.ports.out.UserValidationPort {
    private final RestTemplate restTemplate;

    @Value("${user.validation.service.url}")
    private String userValidationServiceUrl;

    @Override
    public boolean validateUserInHome(UUID guestId, UUID homeId) {
        String url = UriComponentsBuilder.fromUriString(userValidationServiceUrl + "/get/memberHome")
                .queryParam("personId", guestId)
                .queryParam("homeId", homeId)
                .toUriString();

        return restTemplate.getForObject(url, MemberHomeDTO.class) != null;
    }
}
