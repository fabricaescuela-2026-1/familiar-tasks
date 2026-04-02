package com.udea.usermembershipservice.aplication.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;

public interface IMemberHomeRepositoryPort {

    public void saveMemberHome(UUID homeId, UUID personId, UUID rol);
    public void deleteMemberHome(UUID homeId, UUID personId);
    public Optional<MemberHomeDto> getMemberHome(UUID personId, UUID homeId);
    public List<MemberDto> getAllMemberHome(UUID homeId);
    public void updateRoleMemberHome(UUID homeId, UUID personId, UUID newRol);

}
