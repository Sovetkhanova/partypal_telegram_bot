package com.example.it32007telegram.daos.repositories.base;

import com.example.it32007telegram.models.entities.base.BaseEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseEntityRepository<T extends BaseEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    default Optional findByCode(String code) {
        Optional output;

        try {
            output = Optional.of(
                    findOne((root, query, cb) -> cb.equal(root.get("code"), code)));
        } catch (DataAccessException e) {
            output = Optional.empty();
        }

        return output;
    }
}
