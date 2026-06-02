package com.udea.usermembershipservice.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.udea.usermembershipservice.aplication.port.in.IPersonUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.person.PersonDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Tag(name = "Personas", description = "Operaciones para gestionar personas dentro del sistema.")
public class PersonController {

    private final IPersonUseCase personUsecase;
    public PersonController(IPersonUseCase personUsecase) {
        this.personUsecase = personUsecase;
    }


    @Operation(summary = "Buscar persona por email", description = "Consulta una persona especifica a partir de su email.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Persona encontrada correctamente"),
        @ApiResponse(responseCode = "404", description = "Persona no encontrada con el email proporcionado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("search/byEmail")
    public ResponseEntity<PersonDTO> getPersonByEmail(@RequestParam String email) {
        PersonDTO person = personUsecase.getPersonByEmail(email);
        return ResponseEntity.ok(person);
    }

    @Operation(summary = "Buscar todas las personas", description = "Consulta todas las personas registradas en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Personas encontradas correctamente")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("findAll")
    public ResponseEntity<List<PersonDTO>> findAllPersons() {
        List<PersonDTO> persons = personUsecase.getAllPersons();
        return ResponseEntity.ok(persons);
    }

}
