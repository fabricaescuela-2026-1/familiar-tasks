package com.udea.usermembershipservice.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.udea.usermembershipservice.aplication.port.in.IPersonUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Tag(name = "Personas", description = "Operaciones relacionadas con la gestión de personas en el sistema.")
public class PersonController {

    private final IPersonUseCase personUseCase;

    public PersonController(IPersonUseCase personUseCase) {
        this.personUseCase = personUseCase;
    }

    @PostMapping("delete/person")
    @Operation(summary = "Eliminar persona", description = "Elimina una persona existente en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Persona eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro la persona solicitada")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deletePerson(@RequestParam String gmail) {
        personUseCase.deletePerson(gmail);
        return ResponseEntity.ok().build();
    }
}
