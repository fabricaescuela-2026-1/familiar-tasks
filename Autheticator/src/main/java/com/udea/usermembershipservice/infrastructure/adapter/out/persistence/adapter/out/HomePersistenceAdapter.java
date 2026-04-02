package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataHomeJpaRepository;

public class HomePersistenceAdapter implements IHomeRepositoryPort {

    private final SpringDataHomeJpaRepository repository;
    private final HomePersistenceMapper mapper;

    public HomePersistenceAdapter(SpringDataHomeJpaRepository repository, HomePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void saveHome(Home home) {
        var savedHome = repository.save(mapper.toEntity(home));
        if (savedHome == null) {
            throw new RuntimeException("Error saving home");
        }
    }

    @Override
    public List<Home> getAllHomes() {
        return repository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Home> getHomeByName(String name) {
        return repository.findByNameIgnoreCase(name)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Home> getHomeById(UUID idHome) {
        return repository.findById(idHome)
            .map(mapper::toDomain);
    }

    @Override
    public void deleteHome(String name) {
        Home home = repository.findByNameIgnoreCase(name)
            .map(mapper::toDomain)
            .orElseThrow(() -> new RuntimeException("Home not found"));

        repository.deleteById(home.getIdHome());
    }
}
