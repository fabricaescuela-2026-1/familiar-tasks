package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "users", name = "roles")
public class RoleJpaEntity {

    @Id
    @Column(name = "role_id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
