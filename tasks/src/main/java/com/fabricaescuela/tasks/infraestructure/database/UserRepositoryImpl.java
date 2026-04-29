package com.fabricaescuela.tasks.infraestructure.database;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;

import com.fabricaescuela.tasks.domain.ports.out.UserRepository;

public class UserRepositoryImpl implements UserRepository {

    private final UserRepository userRepository;

    public UserRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findUserEntityByUsername(username);
    }


   
}
