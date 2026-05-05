package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(schema = "Users", name = "member_home")
public class MemberHomeJpaEntity {

    @EmbeddedId
    private MemberHomeJpaEntityId id;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;
}
