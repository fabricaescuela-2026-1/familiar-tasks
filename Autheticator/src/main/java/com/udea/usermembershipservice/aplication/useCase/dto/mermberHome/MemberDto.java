package com.udea.usermembershipservice.aplication.useCase.dto.mermberHome;

import java.util.UUID;

public record MemberDto(
    UUID idPerson,
    String namePerson,
    String lastNamePerson,
    String gmail,
    UUID roleId
    
) {
} 
