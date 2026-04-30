package com.fabricaescuela.tasks.infraestructure.database.entyties;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
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
@Entity(name = "guests")
public class UserEntity {
    
    @Id
    @Column(name = "guest_id")
    private UUID userId;

    private String name;

    @Column(name = "last_name")
    private String lastname;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public String getUsername() {
        return this.email;
    }

}
