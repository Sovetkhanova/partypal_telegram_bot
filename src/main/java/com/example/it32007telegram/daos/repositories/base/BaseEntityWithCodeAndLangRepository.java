package com.example.it32007telegram.daos.repositories.base;

import com.example.it32007telegram.models.entities.base.BaseEntityWithCodeAndName;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.Optional;

@NoRepositoryBean
public interface BaseEntityWithCodeAndLangRepository<T extends BaseEntityWithCodeAndName> extends BaseEntityRepository<T> {
    @Override
    Optional<T> findByCode(String code);

    Collection<T> findByCodeIn(Collection<String> codes);
}
