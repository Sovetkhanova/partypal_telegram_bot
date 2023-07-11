package com.example.it32007telegram.models.entities.base;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString
@Table(name = "country", schema = "partypal_location")
public class Country extends BaseEntityWithCode {
    @Column(name = "name")
    private String name;

    public Country() {

    }

}
