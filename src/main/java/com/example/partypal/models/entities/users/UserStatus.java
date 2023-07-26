package com.example.partypal.models.entities.users;

import com.example.partypal.models.entities.base.BaseEntityWithCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString
@Table(name = "user_status", schema = "partypal_user")
public class UserStatus extends BaseEntityWithCode {

    @Column(name = "name")
    private String name;

    public UserStatus() {

    }

    public enum Code {
        ACTIVE,
        INACTIVE,
        BLOCKED
    }
}
