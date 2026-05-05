package com.udea.usermembershipservice.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.udea.usermembershipservice.aplication.port.in.ICreateRoleUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.role.CreateRoleDto;
import com.udea.usermembershipservice.aplication.useCase.dto.role.RoleDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Roles", description = "Operaciones para crear, consultar y eliminar roles de usuario.")
public class RoleController {

    private final ICreateRoleUseCase createRoleUseCase;

    public RoleController(ICreateRoleUseCase createRoleUseCase) {
        this.createRoleUseCase = createRoleUseCase;
    }

    @Operation(summary = "Registrar rol", description = "Crea un nuevo rol dentro del sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos para registrar el rol")
    })
    @PostMapping("registerRole")
    public ResponseEntity<Void> registerRole(@RequestBody CreateRoleDto createRoleDto, @RequestParam String gmail) {
        createRoleUseCase.createdRole(createRoleDto, gmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar roles", description = "Obtiene todos los roles disponibles en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de roles obtenida correctamente")
    })
    @GetMapping("getRoles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(createRoleUseCase.geatAllRoles());
    }

    @Operation(summary = "Buscar rol por nombre", description = "Consulta un rol especifico a partir de su nombre.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol encontrado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro el rol solicitado")
    })
    @GetMapping("getRoleByName")
    public ResponseEntity<RoleDto> getRoleByName(@RequestParam String name) {
        return ResponseEntity.ok(createRoleUseCase.getRoleByName(name));
    }

    @Operation(summary = "Eliminar rol", description = "Elimina un rol usando su nombre como criterio de busqueda.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro el rol a eliminar")
    })
    @PostMapping("deleteRole")
    public ResponseEntity<Void> deleteRole(@RequestParam String name) {
        createRoleUseCase.deleteRole(name);
        return ResponseEntity.ok().build();
    }
}
