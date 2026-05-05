package com.udea.usermembershipservice.aplication.port.in;

import java.util.List;

import com.udea.usermembershipservice.aplication.useCase.dto.role.CreateRoleDto;
import com.udea.usermembershipservice.aplication.useCase.dto.role.RoleDto;

public interface ICreateRoleUseCase {

    void createdRole(CreateRoleDto createRoleDto, String gmail);

    List<RoleDto> geatAllRoles();

    RoleDto getRoleByName(String name);


    void deleteRole(String name);
}
