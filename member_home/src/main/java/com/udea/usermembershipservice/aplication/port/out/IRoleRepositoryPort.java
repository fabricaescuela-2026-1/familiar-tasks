package com.udea.usermembershipservice.aplication.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.domain.model.Role;

public interface IRoleRepositoryPort {

    void saveRole(Role role);

    List<Role> getAllRoles();

    Optional<Role> getRoleByName(String name);

    Optional<Role> getRoleById(UUID idRole);


    void deleteRole(String name);
}
