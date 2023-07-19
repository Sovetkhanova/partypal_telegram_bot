package com.example.it32007telegram.models.entities.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.example.it32007telegram.models.entities.base.LangContainer.getGlobalLanguages;
@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
public abstract class BaseEntityWithCodeAndName extends BaseEntity {
    @NotBlank
    protected String code;

    @Override
    @NotNull
    public Long getId() {
        return super.getId();
    }

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore", name = "languages")
    @JsonIgnore
    private Map<String, String> languages = new HashMap<>();

    @NotNull
    private String name;

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }


    public @NotNull String getName() {
        try {
            if (!getGlobalLanguages().isEmpty() && this.languages.get(getGlobalLanguages()) != null)
                return this.name = doTrim(this.languages.get(getGlobalLanguages())).replace("\"", "");
            return this.name;
        } catch (Exception e) {
            return this.name;
        }
    }

    private String doTrim(String name) {
        return name != null ? name.trim() : null;
    }

}
