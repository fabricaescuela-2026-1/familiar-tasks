package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.domain.model.Role;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.RolePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataRoleJpaRepository;

public class RolePersistenceAdapter implements IRoleRepositoryPort {

    private final SpringDataRoleJpaRepository repository;
    private final RolePersistenceMapper mapper;

    public RolePersistenceAdapter(SpringDataRoleJpaRepository repository, RolePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void saveRole(Role role) {
        var savedRole = repository.save(mapper.toEntity(role));
        if (savedRole == null) {
            throw new RuntimeException("Error saving role");
        }
    }

    @Override
    public List<Role> getAllRoles() {
        return repository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return repository.findByNameIgnoreCase(name).map(mapper::toDomain);
    }

    @Override
    public Optional<Role> getRoleById(UUID idRole) {
        return repository.findById(idRole)
            .map(mapper::toDomain);
    }


    @Override
    public void deleteRole(String name) {
        Role role = repository.findByNameIgnoreCase(name)
            .map(mapper::toDomain)
            .orElseThrow(() -> new RuntimeException("Role not found"));

        repository.deleteById(role.getIdRole());
    }
}
