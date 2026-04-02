package com.udea.usermembershipservice.aplication.useCase.dto.person;

public record CreatePersonDto(
    String name,
    String lastName,
    String email,
    String password
) {
} 
