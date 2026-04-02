package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MemberHomeJpaEntityId implements Serializable {

    @Column(name = "home_id", nullable = false, updatable = false)
    private UUID homeId;

    @Column(name = "person_id", nullable = false, updatable = false)
    private UUID personId;
}
