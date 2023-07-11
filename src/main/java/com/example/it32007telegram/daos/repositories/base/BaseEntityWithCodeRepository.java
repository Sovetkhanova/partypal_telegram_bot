package com.example.it32007telegram.daos.repositories.base;

import com.example.it32007telegram.models.entities.base.BaseEntityWithCode;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.Optional;

@NoRepositoryBean
public interface BaseEntityWithCodeRepository<T extends BaseEntityWithCode> extends BaseEntityRepository<T> {
    @Override
    Optional<T> findByCode(String code);

    Collection<T> findByCodeIn(Collection<String> codes);
}
