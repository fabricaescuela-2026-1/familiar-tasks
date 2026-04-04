package com.udea.usermembershipservice.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.RestController;

import com.udea.usermembershipservice.aplication.port.in.ICreateUserUseCase;
import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginDto;
import com.udea.usermembershipservice.aplication.useCase.dto.person.CreatePersonDto;
import com.udea.usermembershipservice.aplication.useCase.dto.person.PersonDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Tag(name = "Usuarios", description = "Operaciones para registrar, consultar y eliminar usuarios del sistema.")
public class RegisterPersonController {

    public ICreateUserUseCase createUserUseCase;
    public ILoginUserCase loginUsercase;

    public RegisterPersonController(ICreateUserUseCase createUserUseCase, ILoginUserCase loginUsercase) {
        this.createUserUseCase = createUserUseCase;
        this.loginUsercase = loginUsercase;
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con la informacion enviada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos para registrar el usuario")
    })
    @PostMapping("register")
    public ResponseEntity<Void> registerPerson(@RequestBody CreatePersonDto createPersonDto) {
        createUserUseCase.createdUser(createPersonDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    })
    @GetMapping("getUsers")
    public ResponseEntity<List<PersonDto>> getAllUsers() {
        return ResponseEntity.ok(createUserUseCase.geatAllUsers());
    }

    @Operation(summary = "Buscar usuario por correo", description = "Consulta la informacion de un usuario usando su correo electronico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro un usuario con el correo indicado")
    })
    @GetMapping("GetUserByEmail")
    public ResponseEntity<PersonDto> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(createUserUseCase.getUserByEmail(email));
    }


    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario a partir de su correo electronico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro el usuario a eliminar")
    })
    @PostMapping("deleteUser")   
    public ResponseEntity<Void> deleteUser(@RequestBody LoginDto loginDto) {
        createUserUseCase.deleteUser(loginDto);
        return ResponseEntity.ok().build();
    }    
}
