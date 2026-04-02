package com.udea.usermembershipservice.aplication.useCase;

import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.port.out.IPasswordEncoderPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;
import com.udea.usermembershipservice.aplication.useCase.exception.LoginException;
import com.udea.usermembershipservice.domain.model.Person;

public class LoginUserCase implements ILoginUserCase {

    IPersonRepositoryPort personRepositoryPort;
    IPasswordEncoderPort passwordEncoderport;

        public LoginUserCase(IPersonRepositoryPort personRepositoryPort, IPasswordEncoderPort passwordEncoderport) {
            this.personRepositoryPort = personRepositoryPort;
            this.passwordEncoderport = passwordEncoderport;
        }

    @Override
    public LoginResultDto login(LoginDto loginDto) {
        try {
            Person person = personRepositoryPort.getUserByEmail(loginDto.email()).orElseThrow(() -> new RuntimeException("Email or password invalid"));

            if (passwordEncoderport.matches(loginDto.password(), person.getPassword())) {
            return new LoginResultDto(true, "Login successful");
            } else {
            return new LoginResultDto(false, "Email or password invalid");
            }
        } catch (Exception e) {
            throw new LoginException("Error during login", e);
        }
        
    }


}
