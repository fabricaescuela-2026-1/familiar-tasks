package com.udea.usermembershipservice.aplication.useCase.dto.person;

import java.util.List;
import java.util.UUID;

public record PersonDTO(
    UUID id,
    String name,
    String email,
    boolean active,
    List<String> homes,
    List<String> roles
) {
} 
