package com.example.partypal.models.entities.base;

import lombok.*;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class BaseEntityWithCode extends BaseEntity {
    @NotBlank
    private String code;

    public void setCode(String code) {
        this.code = code != null ? code.trim() : null;
    }
}
