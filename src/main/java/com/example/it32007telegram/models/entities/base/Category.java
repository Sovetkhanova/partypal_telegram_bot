package com.example.it32007telegram.models.entities.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "category", schema = "partypal_event")
@AllArgsConstructor
@Cacheable
@EqualsAndHashCode(callSuper = true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Category extends BaseEntityWithCode {

    @Column(name = "name")
    private String name;

    public Category() {

    }

    public enum RoleCode {
        Cinema,
        Sport,
        Culture,
        Restaurant,
        Education,
        Party
    }

}
