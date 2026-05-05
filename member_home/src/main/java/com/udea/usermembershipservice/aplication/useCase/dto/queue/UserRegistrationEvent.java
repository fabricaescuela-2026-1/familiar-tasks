package com.udea.usermembershipservice.aplication.useCase.dto.queue;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRegistrationEvent(
    @JsonProperty("userId") String userId,
    @JsonProperty("name") String name,
    @JsonProperty("lastname") String lastname,
    @JsonProperty("email") String email,
    @JsonProperty("passwordHash") String passwordHash,
    @JsonProperty("createdAt") String createdAt
) {}
