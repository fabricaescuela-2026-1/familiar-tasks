package com.udea.usermembershipservice.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.udea.usermembershipservice.aplication.port.in.ICreateHomeUseCase;
import com.udea.usermembershipservice.aplication.port.in.ICreatedMemberHome;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.home.HomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Hogares", description = "Operaciones para registrar, consultar y eliminar hogares, así como listar sus miembros.")
public class HomeController {

    private final ICreateHomeUseCase createHomeUseCase;
    private final ICreatedMemberHome createdMemberHome;

    public HomeController(ICreateHomeUseCase createHomeUseCase, ICreatedMemberHome createdMemberHome) {
        this.createHomeUseCase = createHomeUseCase;
        this.createdMemberHome = createdMemberHome;
    }

    @Operation(summary = "Registrar hogar", description = "Crea un nuevo hogar con la informacion enviada en el cuerpo de la solicitud.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hogar registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos para registrar el hogar")
    })
    @PostMapping("registerHome")
    public ResponseEntity<Void> registerHome(@RequestBody CreateHomeDto createHomeDto) {
        createHomeUseCase.createdHome(createHomeDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar hogares", description = "Obtiene la lista completa de hogares registrados en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de hogares obtenida correctamente")
    })
    @GetMapping("getHomes")
    public ResponseEntity<List<HomeDto>> getAllHomes() {
        return ResponseEntity.ok(createHomeUseCase.geatAllHomes());
    }

    @Operation(summary = "Buscar hogar por nombre", description = "Consulta un hogar especifico a partir de su nombre.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hogar encontrado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro un hogar con el nombre indicado")
    })
    @GetMapping("getHomeByName")
    public ResponseEntity<HomeDto> getHomeByName(@RequestParam String name) {
        return ResponseEntity.ok(createHomeUseCase.getHomeByName(name));
    }


    @Operation(summary = "Eliminar hogar", description = "Elimina un hogar usando su nombre como criterio de busqueda.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hogar eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro el hogar a eliminar")
    })
    @PostMapping("deleteHome")
    public ResponseEntity<Void> deleteHome(@RequestParam String name) {
        createHomeUseCase.deleteHome(name);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar miembros de un hogar", description = "Obtiene todos los miembros asociados a un hogar especifico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembros del hogar obtenidos correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontro el hogar consultado")
    })
    @GetMapping("GetMemberHome")
    public ResponseEntity<List<MemberDto>> getMemberHome(@RequestParam String nameHome) {
        return ResponseEntity.ok().body(createdMemberHome.getAllMemberHome(nameHome));
    }
    
}
