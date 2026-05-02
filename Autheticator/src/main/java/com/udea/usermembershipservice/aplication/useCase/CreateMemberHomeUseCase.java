package com.udea.usermembershipservice.aplication.useCase;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.udea.usermembershipservice.aplication.port.in.ICreatedMemberHome;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;

public class CreateMemberHomeUseCase implements ICreatedMemberHome{

    private static final String HOME_NOT_FOUND = "Home not found";
    private static final String PERSON_NOT_FOUND = "Person not found";
    private static final String ROLE_NOT_FOUND = "Role not found";

    IHomeRepositoryPort homeRepositoryPort;
    IPersonRepositoryPort personRepositoryPort;
    IRoleRepositoryPort roleRepositoryPort;
    IMemberHomeRepositoryPort memberHomeRepositoryPort;
    

    public CreateMemberHomeUseCase(IHomeRepositoryPort homeRepositoryPort, IPersonRepositoryPort personRepositoryPort, IRoleRepositoryPort roleRepositoryPort, IMemberHomeRepositoryPort memberHomeRepositoryPort) {
        this.homeRepositoryPort = homeRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.memberHomeRepositoryPort = memberHomeRepositoryPort;
    }

    @Override
    public void createdMemberHome(String gmail, String rol, String nameHogar) {
        try {
            var home = homeRepositoryPort.getHomeByName(nameHogar).orElseThrow(() -> new RuntimeException(HOME_NOT_FOUND));
        var person = personRepositoryPort.getUserByEmail(gmail).orElseThrow(() -> new RuntimeException(PERSON_NOT_FOUND));
        var role = roleRepositoryPort.getRoleByName(rol).orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));

        memberHomeRepositoryPort.saveMemberHome(home.getIdHome(), person.getIdPerson(), role.getIdRole());
        } catch (Exception e) {
            throw new PersistenceException("Error saving member home", e);
        }
        
    }

    @Override
    public void deleteMemberHome(String nameHome, String gmail) {       
        try {
            var home = homeRepositoryPort.getHomeByName(nameHome).orElseThrow(() -> new RuntimeException(HOME_NOT_FOUND));
        var person = personRepositoryPort.getUserByEmail(gmail).orElseThrow(() -> new RuntimeException(PERSON_NOT_FOUND));

        memberHomeRepositoryPort.deleteMemberHome(home.getIdHome(), person.getIdPerson());
        } catch (Exception e) {
            throw new PersistenceException("Error deleting member home", e);
        }
    }

    @Override
    public CompletableFuture<MemberHomeDto> getMemberHome(UUID personId, UUID homeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MemberHomeDto memberHome = memberHomeRepositoryPort.getMemberHome(personId, homeId).orElseThrow(() -> new RuntimeException("Member home not found"));
                return memberHome;
            } catch (Exception e) {
                throw new SearchException("Error getting member home", e);
            }
        });
    }

    @Override
    public List<MemberDto> getAllMemberHome(String nameHome) {
        try {
            var home = homeRepositoryPort.getHomeByName(nameHome).orElseThrow(() -> new RuntimeException(HOME_NOT_FOUND));
            var members = memberHomeRepositoryPort.getAllMemberHome(home.getIdHome());
        
        return members;
        } catch (Exception e) {
            throw new SearchException("Error getting all member home", e); 
        }
        
    }

    @Override
    public void updateRoleMemberHome(String nameHome, String gmail, String newRol, String gmailAdmin) {
        try {
            var home = homeRepositoryPort.getHomeByName(nameHome).orElseThrow(() -> new RuntimeException(HOME_NOT_FOUND));
            var person = personRepositoryPort.getUserByEmail(gmail).orElseThrow(() -> new RuntimeException(PERSON_NOT_FOUND));
            var role = roleRepositoryPort.getRoleByName(newRol).orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
            var admin = personRepositoryPort.getUserByEmail(gmailAdmin).orElseThrow(() -> new RuntimeException("Admin not found"));
            var adminRole = memberHomeRepositoryPort.getMemberHome(admin.getIdPerson(), home.getIdHome()).orElseThrow(() -> new RuntimeException("Admin is not a member of the home"));

            var roleIdAdmin = roleRepositoryPort.getRoleByName("Administrador").orElseThrow(() -> new RuntimeException("Admin role not found"));

            if (!adminRole.roleId().equals(roleIdAdmin.getIdRole())) {
                throw new RuntimeException("User is not an admin of the home");
            }

            if (person.getIdPerson().equals(admin.getIdPerson())) {
                long adminCount = memberHomeRepositoryPort.getAllMemberHome(home.getIdHome()).stream()
                        .filter(m -> m.roleId().equals(roleIdAdmin.getIdRole()))
                        .count();
                if (adminCount <= 1) {
                    throw new RuntimeException("Cannot remove the only administrator of the home");
                }
            }

            memberHomeRepositoryPort.updateRoleMemberHome(home.getIdHome(), person.getIdPerson(), role.getIdRole());
        } catch (Exception e) {
            throw new PersistenceException("Error updating role member home", e);
        }
    
    }

}
