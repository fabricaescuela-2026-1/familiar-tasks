package com.udea.usermembershipservice.aplication.port.in;

import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;

public interface ILoginUserCase {
    
    public LoginResultDto login(LoginDto loginDto);
}
