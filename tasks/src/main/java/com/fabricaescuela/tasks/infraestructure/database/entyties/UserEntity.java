package com.fabricaescuela.tasks.infraestructure.database.entyties;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity(name = "users")
public class UserEntity {
    
    @Id
    private UUID userId;
    private String name;
    private String lastname;
    private String email;
    private String passwordHash;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    public String getUsername() {
        return this.email;
    }

}
