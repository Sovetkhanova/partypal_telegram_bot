package com.example.it32007telegram.models.entities.users;

import com.example.it32007telegram.models.entities.base.BaseEntityWithCodeAndName;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "role", schema = "partypal_user")
@AllArgsConstructor
@Cacheable
@EqualsAndHashCode(callSuper = true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role extends BaseEntityWithCodeAndName {

    public enum RoleCode {
        ADMIN,
        USER
    }

}
