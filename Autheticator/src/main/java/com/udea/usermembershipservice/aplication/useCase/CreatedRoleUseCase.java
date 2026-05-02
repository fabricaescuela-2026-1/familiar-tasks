package com.udea.usermembershipservice.aplication.useCase;

import java.util.List;
import java.util.UUID;

import com.udea.usermembershipservice.aplication.port.in.ICreateRoleUseCase;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.dto.role.CreateRoleDto;
import com.udea.usermembershipservice.aplication.useCase.dto.role.RoleDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.model.Role;

public class CreatedRoleUseCase implements ICreateRoleUseCase {

    private final IRoleRepositoryPort roleRepositoryPort;
    private final IPersonRepositoryPort personRepositoryPort;  

    public CreatedRoleUseCase(IRoleRepositoryPort roleRepositoryPort, IPersonRepositoryPort personRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
    }

    @Override
    public void createdRole(CreateRoleDto createRoleDto, String gmail) {
        try {
            if (roleRepositoryPort.getRoleByName(createRoleDto.name()).isPresent()) {
                throw new IllegalArgumentException("Role with this name already exists");
            }

            if(personRepositoryPort.getUserByEmail(gmail).isEmpty()) {
                throw new IllegalArgumentException("User not registered");
            }

            Role role = Role.create(UUID.randomUUID(), createRoleDto.name());
            roleRepositoryPort.saveRole(role);
        } catch (Exception e) {
            throw new PersistenceException("Error saving role", e);
        }
    }

    @Override
    public List<RoleDto> geatAllRoles() {
        try {
            return roleRepositoryPort.getAllRoles().stream()
                .map(role -> new RoleDto(role.getIdRole(), role.getName()))
                .toList();
        } catch (Exception e) {
            throw new SearchException("Error getting all roles", e);
        }
    }

    @Override
    public RoleDto getRoleByName(String name) {
        try{
            Role role = roleRepositoryPort.getRoleByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));

            return new RoleDto(role.getIdRole(), role.getName());
        } catch (Exception e) {
            throw new SearchException("Error getting role by name", e);
        }
    }


    @Override
    public void deleteRole(String name) {
        try {
            roleRepositoryPort.deleteRole(name);
        } catch (Exception e) {
            throw new PersistenceException("Error deleting role", e);
        }
    }
}
