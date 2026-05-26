package com.udea.usermembershipservice.aplication.useCase;

import com.udea.usermembershipservice.aplication.port.in.IPersonUseCase;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;

public class PersonUsecase implements IPersonUseCase {

    private final IPersonRepositoryPort personRepositoryPort;

    public PersonUsecase(IPersonRepositoryPort personRepositoryPort) {
        this.personRepositoryPort = personRepositoryPort;
    }

    @Override
    public void deletePerson(String gmail) {
        try {
            personRepositoryPort.getUserByEmail(gmail).orElseThrow(() -> new RuntimeException("Usuario con correo: " + gmail + " no encontrado"));    
            personRepositoryPort.deleteUserByEmail(gmail);
        } catch (Exception e) {
            throw new PersistenceException("Error eliminado el usuario con correo: " + gmail, e);
        }
    }
}
