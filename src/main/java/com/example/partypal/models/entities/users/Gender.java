package com.example.partypal.models.entities.users;

import com.example.partypal.models.entities.base.BaseEntityWithCodeAndName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString
@Table(name = "gender", schema = "partypal_user")
public class Gender extends BaseEntityWithCodeAndName {

    public enum Code {
        MALE,
        FEMALE,
        UNDEFINED
    }
}
