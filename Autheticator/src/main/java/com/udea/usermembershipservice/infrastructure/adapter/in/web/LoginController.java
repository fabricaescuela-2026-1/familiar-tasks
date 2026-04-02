package com.udea.usermembershipservice.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.RestController;

import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Tag(name = "Autenticacion", description = "Operaciones para iniciar sesion y validar credenciales de los usuarios.")
public class LoginController {

    public ILoginUserCase loginUsercase;
    public LoginController(ILoginUserCase loginUsercase) {
        this.loginUsercase = loginUsercase;
    }



    @Operation(summary = "Iniciar sesion", description = "Valida las credenciales del usuario y devuelve la informacion necesaria para la autenticacion.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas"),
        @ApiResponse(responseCode = "400", description = "Solicitud de inicio de sesion invalida")
    })
    @PostMapping("login")
    public ResponseEntity<LoginResultDto> login(@RequestBody LoginDto loginDto) {
        try {
            return ResponseEntity.ok(loginUsercase.login(loginDto));
        } catch (Exception e) {
            throw new RuntimeException("Error logging in", e);
        }
    }
    
}
